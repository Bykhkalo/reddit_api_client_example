package com.bykhkalo.redditapiclient.model

import com.google.gson.annotations.SerializedName


data class Data (

	@SerializedName("modhash") val modhash : String,
	@SerializedName("dist") val dist : Int,
	@SerializedName("children") val postItemList : List<Children>,
	@SerializedName("after") val after : String,
	@SerializedName("before") val before : String
)