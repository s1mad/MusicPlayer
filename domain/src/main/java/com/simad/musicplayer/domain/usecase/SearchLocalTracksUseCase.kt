package com.simad.musicplayer.domain.usecase

import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.repository.LocalTrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchLocalTracksUseCase @Inject constructor(
    private val repo: LocalTrackRepository
) {
    operator fun invoke(query: String): Flow<Result<List<Track>>> {
        return repo.searchTracks(query)
    }
}