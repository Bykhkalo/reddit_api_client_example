package com.bykhkalo.redditapiclient.rest

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class API {

    companion object{
        const val BASE_URL = "https://www.reddit.com/"
    }

    private lateinit var redditApi: RedditApi

    fun getRedditApi(): RedditApi {

        val gson = GsonBuilder()
            .setLenient()
            .create()

        if (!::redditApi.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            redditApi = retrofit.create(RedditApi::class.java)
        }


        return redditApi;
    }

}