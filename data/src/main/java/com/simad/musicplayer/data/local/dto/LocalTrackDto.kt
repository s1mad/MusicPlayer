package com.simad.musicplayer.data.local.dto

import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.model.TrackSource

data class LocalTrackDto(
    val filePath: String,
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Int,
    val coverPath: String? = null
)

fun LocalTrackDto.toDomain(): Track {
    return Track(
        id = filePath,
        title = title,
        artist = artist,
        album = album,
        coverUrl = coverPath,
        duration = duration,
        source = TrackSource.Local(filePath)
    )
}