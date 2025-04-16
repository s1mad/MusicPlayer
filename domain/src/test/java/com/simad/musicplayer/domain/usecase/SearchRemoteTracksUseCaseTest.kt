package com.simad.musicplayer.domain.usecase

import app.cash.turbine.test
import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.model.TrackSource
import com.simad.musicplayer.domain.repository.RemoteTrackRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals

class SearchRemoteTracksUseCaseTest {

    private lateinit var repository: RemoteTrackRepository
    private lateinit var useCase: SearchRemoteTracksUseCase

    private val testTrack = Track(
        id = "1",
        title = "Test Song",
        artist = "Test Artist",
        album = "Test Album",
        coverUrl = "http://example.com/cover.jpg",
        duration = 180,
        source = TrackSource.Remote("http://example.com/preview.mp3")
    )

    @Before
    fun setUp() {
        repository = mock()
        useCase = SearchRemoteTracksUseCase(repository)
    }

    @Test
    fun `should emit tracks for valid query`() = runTest {

        val query = "test"
        val expectedTracks = listOf(testTrack)
        `when`(repository.searchTracks(query)).thenReturn(flowOf(Result.success(expectedTracks)))


        useCase(query).test {
            val result = awaitItem()
            assertEquals(Result.success(expectedTracks), result)
            awaitComplete()
        }
    }

    @Test
    fun `should emit failure when repository fails`() = runTest {

        val query = "test"
        val exception = RuntimeException("Search error")
        `when`(repository.searchTracks(query)).thenReturn(flowOf(Result.failure(exception)))


        useCase(query).test {
            val result = awaitItem()
            assertEquals(exception, result.exceptionOrNull())
            awaitComplete()
        }
    }
}