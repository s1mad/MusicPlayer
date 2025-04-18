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

class SearchLocalTracksUseCaseTest {

    private lateinit var repository: LocalTrackRepository
    private lateinit var useCase: SearchLocalTracksUseCase

    private val testTrack = Track(
        id = "1",
        title = "Local Song",
        artist = "Local Artist",
        album = "Local Album",
        coverUrl = "http://example.com/local_cover.jpg",
        duration = 200,
        source = TrackSource.Local("local/path/to/file.mp3")
    )

    @Before
    fun setUp() {
        repository = mock()
        useCase = SearchLocalTracksUseCase(repository)
    }

    @Test
    fun `should emit tracks for valid query`() = runTest {
        val query = "local"
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
        val query = "local"
        val exception = RuntimeException("Local search error")
        `when`(repository.searchTracks(query)).thenReturn(flowOf(Result.failure(exception)))

        useCase(query).test {
            val result = awaitItem()
            assertEquals(exception, result.exceptionOrNull())
            awaitComplete()
        }
    }
}