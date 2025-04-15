package com.simad.musicplayer.domain.repository

import com.simad.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface RemoteTrackRepository {
    fun chartTracks(): Flow<Result<List<Track>>>
    fun searchTracks(query: String): Flow<Result<List<Track>>>
}