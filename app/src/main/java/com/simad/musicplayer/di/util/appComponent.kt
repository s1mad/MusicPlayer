package com.simad.musicplayer.di.util

import android.content.Context
import com.simad.musicplayer.MusicPlayerApplication
import com.simad.musicplayer.di.AppComponent

val Context.appComponent: AppComponent
    get() = (applicationContext as? MusicPlayerApplication)?.appComponent
        ?: error("Application is not of type MusicPlayerApplication.")
