package com.bykhkalo.redditapiclient.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bykhkalo.redditapiclient.model.RedditPost
import androidx.paging.DataSource

@Dao
interface RedditPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<RedditPost>)

    @Query("SELECT * FROM posts WHERE subreddit = :subreddit ORDER BY indexInResponse ASC")
    fun getPosts(subreddit : String) : DataSource.Factory<Int, RedditPost>

    @Query("SELECT MAX(indexInResponse) + 1 FROM posts WHERE subreddit = :subreddit")
    fun getNextIndex(subreddit: String) : Int
}