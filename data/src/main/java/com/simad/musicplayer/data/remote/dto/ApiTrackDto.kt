package com.simad.musicplayer.data.remote.dto

import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.model.TrackSource
import kotlinx.serialization.Serializable

@Serializable
data class ApiTracksResponse(val data: List<ApiTrackDto>)

@Serializable
data class ApiTrackDto(
    val id: Long,
    val title: String,
    val artist: ApiArtistDto,
    val album: ApiAlbumDto? = null,
    val preview: String,
    val duration: Int
)

@Serializable
data class ApiArtistDto(val name: String)

@Serializable
data class ApiAlbumDto(val id: Long, val title: String, val cover_big: String?)

fun ApiTrackDto.toDomain(): Track {
    return Track(
        id = id.toString(),
        title = title,
        artist = artist.name,
        album = album?.title,
        albumId = album?.id?.toString(),
        coverUrl = album?.cover_big,
        duration = duration,
        source = TrackSource.Remote(preview)
    )
}