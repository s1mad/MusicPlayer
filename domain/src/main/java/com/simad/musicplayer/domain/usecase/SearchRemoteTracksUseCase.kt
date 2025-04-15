package com.simad.musicplayer.domain.usecase

import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.repository.RemoteTrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRemoteTracksUseCase @Inject constructor(
    private val repo: RemoteTrackRepository
) {
    operator fun invoke(query: String): Flow<Result<List<Track>>> {
        return repo.searchTracks(query)
    }
}