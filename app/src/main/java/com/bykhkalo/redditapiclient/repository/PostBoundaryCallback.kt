package com.bykhkalo.redditapiclient.repository

import android.util.Log
import androidx.paging.PagedList
import com.bykhkalo.redditapiclient.model.RedditPost
import com.bykhkalo.redditapiclient.model.RedditResponse
import com.bykhkalo.redditapiclient.rest.RedditApi
import com.bykhkalo.redditapiclient.utils.DebugUtils.Companion.TAG
import com.memebattle.pagingwithrepository.domain.repository.network.createStatusLiveData
import com.memebattle.pwc.helper.PwcPagingRequestHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class PostBoundaryCallback(
    private val networkDataSource: RedditApi,
    private val handleResponse: (RedditResponse?) -> Unit,
    private val ioExecutor: Executor,
    private val networkPageSize: Int,
) : PagedList.BoundaryCallback<RedditPost>() {

    val helper = PwcPagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()


    override fun onZeroItemsLoaded() {
        Log.d(TAG, "BoundaryCallback: onZeroItemsLoaded: ")
        helper.runIfNotRunning(PwcPagingRequestHelper.RequestType.BEFORE) {
            networkDataSource.getTopPosts(networkPageSize)
                .enqueue(createWebserviceCallback(it))
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: RedditPost) {
        // ignored, since we only ever append to what's in the DB
    }

    override fun onItemAtEndLoaded(itemAtEnd: RedditPost) {
        helper.runIfNotRunning(PwcPagingRequestHelper.RequestType.AFTER) {
            networkDataSource.getTopPostsAfter(
                after = itemAtEnd.name,
                limit = networkPageSize)
                .enqueue(createWebserviceCallback(it))
        }
    }

    private fun insertItemsIntoDb(
        response: Response<RedditResponse>,
        it: PwcPagingRequestHelper.Request.Callback
    ) {
        Log.d(TAG, "BoundaryCallback.insertItemsIntoDb")
        ioExecutor.execute {
            handleResponse(response.body())
            it.recordSuccess()
        }
    }

    private fun createWebserviceCallback(it: PwcPagingRequestHelper.Request.Callback)
            : Callback<RedditResponse> {
        return object : Callback<RedditResponse> {
            override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
                Log.d(TAG, "BoundaryCallback.onFailure ${t.message}")
                it.recordFailure(t)
            }

            override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {
                Log.d(TAG, "BoundaryCallback.onResponse: loaded: $response")
                insertItemsIntoDb(response, it)
            }

        }
    }


}