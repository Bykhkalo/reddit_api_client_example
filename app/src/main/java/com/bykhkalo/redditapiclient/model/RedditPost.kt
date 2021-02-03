package com.bykhkalo.redditapiclient.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "posts")
data class RedditPost (
	@SerializedName("subreddit_id") val subreddit_id : String,

	@SerializedName("id") val id : String,
	@SerializedName("author") val author : String,

	//Author nickname
	@SerializedName("subreddit") val subreddit : String,

	//Post content text
	@SerializedName("title") val title : String,

	//current identifier for loading next page with after=name
	@SerializedName("name") val name : String,
	@SerializedName("num_comments") val num_comments : Int,

	//Creation date in timestamp
	@SerializedName("created_utc") val created_utc : Int,

	@SerializedName("thumbnail") val thumbnail : String,
	@SerializedName("url_overridden_by_dest") val url_overridden_by_dest : String,
) {
	// to be consistent w/ changing backend order, we need to keep a data like this
	var indexInResponse: Int = -1

}