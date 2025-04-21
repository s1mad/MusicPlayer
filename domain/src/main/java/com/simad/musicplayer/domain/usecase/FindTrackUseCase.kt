package com.simad.musicplayer.domain.usecase

import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.repository.FindTrackRepository
import javax.inject.Inject

class FindTrackUseCase @Inject constructor(
    private val repo: FindTrackRepository
) {
    suspend operator fun invoke(trackId: String): Result<Track> {
        return repo.find(trackId)
    }
}