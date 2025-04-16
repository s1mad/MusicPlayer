package com.simad.musicplayer.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.simad.musicplayer.data.remote.DeezerApi
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideDeezerApi(): DeezerApi {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(DeezerApi::class.java)
    }
}