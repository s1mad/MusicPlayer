package com.simad.musicplayer.domain.usecase

import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.model.TrackSource
import com.simad.musicplayer.domain.repository.FindTrackRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FindTrackUseCaseTest {

    private lateinit var repository: FindTrackRepository
    private lateinit var useCase: FindTrackUseCase

    private val testTrack = Track(
        id = "1",
        title = "Test Song",
        artist = "Test Artist",
        album = "Test Album",
        coverUrl = "http://example.com/cover.jpg",
        duration = 180,
        source = TrackSource.Local("/storage/emulated/0/Music/test.mp3")
    )

    @Before
    fun setUp() {
        repository = mock()
        useCase = FindTrackUseCase(repository)
    }

    @Test
    fun `should return track when repository finds it`() = runTest {
        `when`(repository.find("1")).thenReturn(Result.success(testTrack))

        val result = useCase("1")

        assertTrue(result.isSuccess)
        assertEquals(testTrack, result.getOrNull())
    }

    @Test
    fun `should return failure when repository fails`() = runTest {
        val exception = IllegalStateException("Track not found")
        `when`(repository.find("2")).thenReturn(Result.failure(exception))

        val result = useCase("2")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}