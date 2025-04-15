package com.simad.musicplayer.domain.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String?,
    val coverUrl: String?,
    val duration: Int,
    val source: TrackSource
)

sealed class TrackSource {
    data class Local(val filePath: String) : TrackSource()
    data class Remote(val previewUrl: String) : TrackSource()
}