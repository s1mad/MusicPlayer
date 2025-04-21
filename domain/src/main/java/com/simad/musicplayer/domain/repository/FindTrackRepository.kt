package com.simad.musicplayer.domain.repository

import com.simad.musicplayer.domain.model.Track

interface FindTrackRepository {
    suspend fun find(id: String): Result<Track>
    suspend fun findAlbum(id: String, isRemoteSource: Boolean): Result<List<Track>>
}