package com.bykhkalo.redditapiclient.repository

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.bykhkalo.redditapiclient.App
import com.bykhkalo.redditapiclient.model.RedditPost
import com.bykhkalo.redditapiclient.model.RedditResponse
import com.bykhkalo.redditapiclient.rest.API
import com.bykhkalo.redditapiclient.rest.RedditApi
import com.bykhkalo.redditapiclient.room.RedditDb
import com.bykhkalo.redditapiclient.room.RedditPostDao
import com.bykhkalo.redditapiclient.utils.DebugUtils.Companion.TAG
import com.memebattle.pwc.util.NetworkState
import com.memebattle.pwc.util.PwcListing
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import javax.inject.Inject

class RedditPostsRepositoryImpl(var pageSize: Int, var prefetchDistance: Int = pageSize): RedditPostsRepository {

    init {
        App.appComponent.inject(this)
        Log.d(TAG, "Injecting: ")
    }

    @Inject
    lateinit var roomDb: RedditDb

    @Inject
    lateinit var roomDataSource: RedditPostDao
    private var networkDataSource: RedditApi = API().getRedditApi()

    val ioExecutor = Executors.newSingleThreadExecutor()


    override fun postsOfSubreddit(): PwcListing<RedditPost> {

        Log.d(TAG, "getPosts: $pageSize")
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = PostBoundaryCallback(
            networkDataSource = networkDataSource,
            handleResponse = this::insertResultIntoDb,
            ioExecutor = ioExecutor,
            networkPageSize = pageSize,
        )
        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }
        // We use toLiveData Kotlin extension function here, you could also use LivePagedListBuilder

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setMaxSize(pageSize + 2 * prefetchDistance)
            .setPrefetchDistance(prefetchDistance)
            .setPageSize(pageSize)
            .build()

        val livePagedList = LivePagedListBuilder(roomDataSource.getPosts(), config)
            .setBoundaryCallback(boundaryCallback)
            .build()


        return PwcListing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.helper.retryAllFailed()
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    private fun refresh(): LiveData<NetworkState> {
        Log.d(TAG, "repository.refresh(): ")
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        networkDataSource.getTopPosts(10).enqueue(
            object : Callback<RedditResponse> {
                override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
                    // retrofit calls this on main thread so safe to call set value
                    networkState.value = NetworkState.error(t.message)
                }

                override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {
                    Log.d(TAG, "MainRepository.refresh(): onResponse: pageNum: 1")
                    ioExecutor.execute {
                        roomDb.runInTransaction {
                            roomDataSource.deleteAll()
                            insertResultIntoDb(response.body())
                        }
                        // since we are in bg thread now, post the result.
                        networkState.postValue(NetworkState.LOADED)
                    }
                }
            }
        )
        return networkState
    }

    private fun insertResultIntoDb(body: RedditResponse?) {
        body!!.data.postItemList.let { posts ->
            val start = roomDataSource.getNextIndex()
            val items = posts.mapIndexed { index, imageItem ->
                imageItem.data.indexInResponse = start + index
                imageItem.data
                /*Даний метод визначення позиції елементів
                  у списку де він буде відображатися.

                  У кожного user є поле індекса.

                  Це поле визначає положення елемента у своїй группі
                  Наприклад, група постів Василя чи постів Віктора

                  Типу пам'ять:
                  [пости Віктора(індекси з 11 по 20), пости Василя(індекси з 0 по 10), пости Віктора(індекси з 0 по 10)]

                */

            }

            roomDataSource.insert(items)
        }
    }


    private fun clearDb() {
        ioExecutor.execute {
            roomDb.runInTransaction {
                roomDataSource.deleteAll()
            }
        }
    }
}