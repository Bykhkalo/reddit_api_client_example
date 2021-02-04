package com.bykhkalo.redditapiclient

import android.app.Application
import com.bykhkalo.redditapiclient.di.AppComponent
import com.bykhkalo.redditapiclient.di.DaggerAppComponent
import com.bykhkalo.redditapiclient.di.RoomModule

class App: Application() {

    companion object{
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .roomModule(RoomModule(this))
            .build()
    }

}