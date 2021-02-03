package com.bykhkalo.redditapiclient.model

import com.google.gson.annotations.SerializedName

data class RedditResponse(
    @SerializedName("kind") val kind : String,
    @SerializedName("data") val data : Data
) {
}