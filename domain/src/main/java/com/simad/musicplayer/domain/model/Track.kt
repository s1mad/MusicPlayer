package com.simad.musicplayer.domain.model

import android.net.Uri
import java.io.File

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String?,
    val albumId: String?,
    val coverUrl: String?,
    val duration: Int,
    val source: TrackSource
)

sealed class TrackSource {
    data class Local(val filePath: String) : TrackSource()
    data class Remote(val previewUrl: String) : TrackSource()
}

fun TrackSource.toUri(): Uri = when (this) {
    is TrackSource.Local -> Uri.fromFile(File(filePath))
    is TrackSource.Remote -> Uri.parse(previewUrl)
}