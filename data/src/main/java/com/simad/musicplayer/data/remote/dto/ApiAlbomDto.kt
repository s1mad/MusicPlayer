package com.simad.musicplayer.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiAlbumTracksResponse(
    val tracks: ApiTracksResponse
)