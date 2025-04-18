package com.simad.musicplayer.domain.usecase

import app.cash.turbine.test
import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.model.TrackSource
import com.simad.musicplayer.domain.repository.LocalTrackRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class GetLocalTracksUseCaseTest {

    private lateinit var repository: LocalTrackRepository
    private lateinit var useCase: GetLocalTracksUseCase

    private val testTrack = Track(
        id = "1",
        title = "Downloaded Song",
        artist = "Offline Artist",
        album = "Offline Album",
        coverUrl = "http://example.com/downloaded.jpg",
        duration = 250,
        source = TrackSource.Local("/downloads/song.mp3")
    )

    @Before
    fun setUp() {
        repository = mock()
        useCase = GetLocalTracksUseCase(repository)
    }

    @Test
    fun `should emit all local tracks from repository`() = runTest {
        val expectedTracks = listOf(testTrack)
        `when`(repository.allTracks()).thenReturn(flowOf(Result.success(expectedTracks)))

        useCase().test {
            val result = awaitItem()
            assertEquals(Result.success(expectedTracks), result)
            awaitComplete()
        }
    }

    @Test
    fun `should emit failure when repository fails`() = runTest {
        val exception = RuntimeException("Failed to load local tracks")
        `when`(repository.allTracks()).thenReturn(flowOf(Result.failure(exception)))

        useCase().test {
            val result = awaitItem()
            assertEquals(exception, result.exceptionOrNull())
            awaitComplete()
        }
    }
}