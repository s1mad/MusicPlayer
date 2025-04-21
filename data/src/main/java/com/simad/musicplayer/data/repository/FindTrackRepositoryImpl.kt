package com.simad.musicplayer.data.repository

import com.simad.musicplayer.data.local.LocalTrackDataSource
import com.simad.musicplayer.data.local.dto.toDomain
import com.simad.musicplayer.data.remote.DeezerApi
import com.simad.musicplayer.data.remote.dto.toDomain
import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.repository.FindTrackRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FindTrackRepositoryImpl @Inject constructor(
    private val api: DeezerApi,
    private val localSource: LocalTrackDataSource
) : FindTrackRepository {
    override suspend fun find(id: String): Result<Track> {
        return try {
            val longId = id.toLongOrNull()
            if (longId != null) {
                val apiTrack = api.getTrackById(longId)
                Result.success(apiTrack.toDomain())
            } else {
                val localTracks = localSource.getLocalTracks().first()
                val localTrack = localTracks.find { it.filePath == id }
                return if (localTrack != null) {
                    Result.success(localTrack.toDomain())
                } else {
                    Result.failure(Exception("Трек не найден"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun findAlbum(
        id: String,
        isRemoteSource: Boolean
    ): Result<List<Track>> {
        return try {
            if (isRemoteSource) {
                val response = api.getAlbumTracks(id.toLong())
                Result.success(response.tracks.data.map { it.toDomain() })
            } else {
                val localTracks = localSource.getLocalTracks().first()
                val albumTracks = localTracks.filter { it.album == id }.map { it.toDomain() }
                Result.success(albumTracks)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}