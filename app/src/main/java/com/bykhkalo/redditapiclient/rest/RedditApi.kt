package com.bykhkalo.redditapiclient.rest

import com.bykhkalo.redditapiclient.model.RedditResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RedditApi {

    @GET("/top")
    fun getTopPosts(@Query("after") after: String,
                    @Query("limit") limit: Int): Call<RedditResponse>
}