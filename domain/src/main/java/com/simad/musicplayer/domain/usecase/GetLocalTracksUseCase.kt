package com.simad.musicplayer.domain.usecase

import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.repository.LocalTrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalTracksUseCase@Inject constructor(
    private val repo: LocalTrackRepository
) {
    operator fun invoke(): Flow<Result<List<Track>>> {
        return repo.allTracks()
    }
}