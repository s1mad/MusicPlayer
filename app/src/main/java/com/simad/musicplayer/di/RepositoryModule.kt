package com.simad.musicplayer.di

import com.simad.musicplayer.data.repository.RemoteTrackRepositoryImpl
import com.simad.musicplayer.domain.repository.RemoteTrackRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindRemoteTrackRepository(impl: RemoteTrackRepositoryImpl): RemoteTrackRepository
}