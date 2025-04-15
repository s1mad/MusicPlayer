package com.simad.musicplayer

import android.app.Application
import com.simad.musicplayer.di.AppComponent
import com.simad.musicplayer.di.DaggerAppComponent

class MusicPlayerApplication : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
    }
}