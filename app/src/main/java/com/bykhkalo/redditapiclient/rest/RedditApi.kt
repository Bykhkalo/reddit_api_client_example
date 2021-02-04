package com.bykhkalo.redditapiclient.rest

import com.bykhkalo.redditapiclient.model.RedditResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RedditApi {

    @GET("top.json")
    fun getTopPosts(
        @Query("limit") limit: Int
    ): Call<RedditResponse>

    @GET("top.json")
    fun getTopPostsAfter(
        @Query("after") after: String,
        @Query("limit") limit: Int
    ): Call<RedditResponse>
}