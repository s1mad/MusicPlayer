package com.simad.musicplayer.data.remote

import com.simad.musicplayer.data.remote.dto.ApiTracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerApi {
    @GET("chart/0/tracks")
    suspend fun getChartTracks(): ApiTracksResponse

    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): ApiTracksResponse
}