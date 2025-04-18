package com.simad.musicplayer.data.repository

import com.simad.musicplayer.data.local.LocalTrackDataSource
import com.simad.musicplayer.data.local.dto.toDomain
import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.repository.LocalTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalTrackRepositoryImpl @Inject constructor(
    val source: LocalTrackDataSource
) : LocalTrackRepository {
    override fun allTracks(): Flow<Result<List<Track>>> {
        return source.getLocalTracks().map { localTracks ->
            Result.success(localTracks.map { it.toDomain() })
        }.catch { e ->
            emit(Result.failure(Exception("Storage error: ${e.message}")))
        }
    }

    override fun searchTracks(query: String): Flow<Result<List<Track>>> {
        return source.searchLocalTracks(query).map { localTracks ->
            Result.success(localTracks.map { it.toDomain() })
        }.catch { e ->
            emit(Result.failure(Exception("Storage error: ${e.message}")))
        }
    }
}