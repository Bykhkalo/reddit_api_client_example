package com.bykhkalo.redditapiclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bykhkalo.redditapiclient.repository.RedditPostsRepository
import com.bykhkalo.redditapiclient.repository.RedditPostsRepositoryImpl

class MainViewModel: ViewModel() {

    private var repository: RedditPostsRepository = RedditPostsRepositoryImpl(10)

    private val trigger = MutableLiveData<Boolean>()

    private val repoResult = Transformations.map(trigger) {
        repository.postsOfSubreddit()
    }

    val posts = Transformations.switchMap(repoResult) { it.pagedList }!!
    val networkState = Transformations.switchMap(repoResult) { it.networkState }!!
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }!!

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }

    fun beginLoading(){
        trigger.value = true
    }

}