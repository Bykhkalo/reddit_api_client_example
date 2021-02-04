package com.bykhkalo.redditapiclient.repository

import com.bykhkalo.redditapiclient.model.RedditPost
import com.memebattle.pwc.util.PwcListing

interface RedditPostsRepository {

    fun postsOfSubreddit(): PwcListing<RedditPost>
}