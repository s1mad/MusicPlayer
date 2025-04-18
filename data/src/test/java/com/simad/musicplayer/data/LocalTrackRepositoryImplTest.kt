package com.simad.musicplayer.data

import app.cash.turbine.test
import com.simad.musicplayer.data.local.LocalTrackDataSource
import com.simad.musicplayer.data.local.dto.LocalTrackDto
import com.simad.musicplayer.data.repository.LocalTrackRepositoryImpl
import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.model.TrackSource
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocalTrackRepositoryImplTest {

    private lateinit var dataSource: LocalTrackDataSource
    private lateinit var repository: LocalTrackRepositoryImpl

    private val localTrackDto = LocalTrackDto(
        filePath = "/music/test.mp3",
        title = "Test Track",
        artist = "Test Artist",
        album = "Test Album",
        duration = 300,
        coverPath = "/covers/test.jpg"
    )

    private val expectedTrack = Track(
        id = "/music/test.mp3",
        title = "Test Track",
        artist = "Test Artist",
        album = "Test Album",
        duration = 300,
        coverUrl = "/covers/test.jpg",
        source = TrackSource.Local("/music/test.mp3")
    )

    @Before
    fun setUp() {
        dataSource = mock()
        repository = LocalTrackRepositoryImpl(dataSource)
    }

    @Test
    fun `allTracks should return success result with mapped tracks`() = runTest {
        `when`(dataSource.getLocalTracks()).thenReturn(flow {
            emit(listOf(localTrackDto))
        })

        repository.allTracks().test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(listOf(expectedTrack), result.getOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `allTracks should return failure when exception is thrown`() = runTest {
        `when`(dataSource.getLocalTracks()).thenReturn(flow {
            throw RuntimeException("Disk error")
        })

        repository.allTracks().test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("Storage error") == true)
            awaitComplete()
        }
    }

    @Test
    fun `searchTracks should return success result with mapped tracks`() = runTest {
        val query = "Test"
        `when`(dataSource.searchLocalTracks(query)).thenReturn(flow {
            emit(listOf(localTrackDto))
        })

        repository.searchTracks(query).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(listOf(expectedTrack), result.getOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `searchTracks should return failure when exception is thrown`() = runTest {
        val query = "Test"
        `when`(dataSource.searchLocalTracks(query)).thenReturn(flow {
            throw RuntimeException("Read error")
        })

        repository.searchTracks(query).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("Storage error") == true)
            awaitComplete()
        }
    }
}