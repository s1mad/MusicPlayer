package com.simad.musicplayer.data.repository

import com.simad.musicplayer.data.remote.DeezerApi
import com.simad.musicplayer.data.remote.dto.toDomain
import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.repository.RemoteTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class RemoteTrackRepositoryImpl @Inject constructor(
    private val api: DeezerApi,
): RemoteTrackRepository {
    override fun chartTracks(): Flow<Result<List<Track>>> = flow {
        try {
            val tracks = api.getChartTracks().data.map { it.toDomain() }
            emit(Result.success(tracks))
        } catch (e: IOException) {
            emit(Result.failure(IOException("Network error: ${e.message}")))
        } catch (e: Exception) {
            emit(Result.failure(Exception("Unexpected error: ${e.message}")))
        }
    }

    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val tracks = api.searchTracks(query).data.map { it.toDomain() }
            emit(Result.success(tracks))
        } catch (e: IOException) {
            emit(Result.failure(IOException("Network error: ${e.message}")))
        } catch (e: Exception) {
            emit(Result.failure(Exception("Unexpected error: ${e.message}")))
        }
    }
}