package com.simad.musicplayer.domain.usecase

import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.repository.RemoteTrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRemoteChartTracksUseCase@Inject constructor(
    private val repo: RemoteTrackRepository
) {
    operator fun invoke(): Flow<Result<List<Track>>> {
        return repo.chartTracks()
    }
}