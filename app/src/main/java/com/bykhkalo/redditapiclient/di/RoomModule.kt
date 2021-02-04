package com.bykhkalo.redditapiclient.di

import android.content.Context
import androidx.room.Room
import com.bykhkalo.redditapiclient.room.RedditDb
import com.bykhkalo.redditapiclient.room.RedditPostDao
import com.bykhkalo.redditapiclient.room.RedditPostRoomDataSource
import com.bykhkalo.redditapiclient.room.RedditPostRoomDataSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideDatabase(): RedditDb {
        return Room.databaseBuilder(context, RedditDb::class.java, "database")
            .build()
    }

    @Provides
    fun provideShopItemDao(db: RedditDb): RedditPostDao{
        return db.posts()
    }

}