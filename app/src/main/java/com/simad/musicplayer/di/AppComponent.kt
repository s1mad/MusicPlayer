package com.simad.musicplayer.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.simad.musicplayer.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        ViewModelModule::class,
    ]
)
interface AppComponent {

    fun inject(activity: MainActivity)

    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Application): AppComponent
    }
}