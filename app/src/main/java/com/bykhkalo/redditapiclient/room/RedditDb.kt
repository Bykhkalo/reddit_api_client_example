package com.bykhkalo.redditapiclient.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bykhkalo.redditapiclient.model.RedditPost

@Database(
    entities = [RedditPost::class],
    version = 1,
    exportSchema = false
)
abstract class RedditDb : RoomDatabase(){

    abstract fun posts(): RedditPostDao
}