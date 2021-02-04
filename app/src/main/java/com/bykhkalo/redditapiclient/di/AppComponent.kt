package com.bykhkalo.redditapiclient.di

import com.bykhkalo.redditapiclient.repository.RedditPostsRepository
import com.bykhkalo.redditapiclient.repository.RedditPostsRepositoryImpl
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RoomModule::class])
interface AppComponent {

    fun inject(repository: RedditPostsRepositoryImpl)

}