package com.simad.musicplayer.di

import com.simad.musicplayer.data.repository.FindTrackRepositoryImpl
import com.simad.musicplayer.data.repository.LocalTrackRepositoryImpl
import com.simad.musicplayer.data.repository.RemoteTrackRepositoryImpl
import com.simad.musicplayer.domain.repository.FindTrackRepository
import com.simad.musicplayer.domain.repository.LocalTrackRepository
import com.simad.musicplayer.domain.repository.RemoteTrackRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindRemoteTrackRepository(impl: RemoteTrackRepositoryImpl): RemoteTrackRepository

    @Binds
    @Singleton
    fun bindLocalTrackRepository(impl: LocalTrackRepositoryImpl): LocalTrackRepository

    @Binds
    @Singleton
    fun bindFindTrackRepository(impl: FindTrackRepositoryImpl): FindTrackRepository
}