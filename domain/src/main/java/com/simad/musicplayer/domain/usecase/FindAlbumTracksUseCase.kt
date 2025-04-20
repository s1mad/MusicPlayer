package com.simad.musicplayer.domain.usecase

import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.repository.FindTrackRepository
import javax.inject.Inject

class FindAlbumTracksUseCase @Inject constructor(
    private val repo: FindTrackRepository
) {
    suspend operator fun invoke(albumId: String, isRemoteSource: Boolean): Result<List<Track>> {
        return repo.findAlbum(id = albumId, isRemoteSource = isRemoteSource)
    }
}