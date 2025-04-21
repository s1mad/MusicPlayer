package com.simad.musicplayer.data

import com.simad.musicplayer.data.local.LocalTrackDataSource
import com.simad.musicplayer.data.local.dto.LocalTrackDto
import com.simad.musicplayer.data.remote.DeezerApi
import com.simad.musicplayer.data.remote.dto.ApiAlbumDto
import com.simad.musicplayer.data.remote.dto.ApiArtistDto
import com.simad.musicplayer.data.remote.dto.ApiTrackDto
import com.simad.musicplayer.data.repository.FindTrackRepositoryImpl
import com.simad.musicplayer.domain.model.TrackSource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FindTrackRepositoryImplTest {

    private lateinit var api: DeezerApi
    private lateinit var localDataSource: LocalTrackDataSource
    private lateinit var repository: FindTrackRepositoryImpl

    private val apiTrackDto = ApiTrackDto(
        id = 123L,
        title = "Remote Song",
        artist = ApiArtistDto(name = "Remote Artist"),
        album = ApiAlbumDto(title = "Remote Album", cover = "http://example.com/cover.jpg"),
        preview = "http://example.com/preview.mp3",
        duration = 200
    )

    private val localTrackDto = LocalTrackDto(
        filePath = "/music/local_song.mp3",
        title = "Local Song",
        artist = "Local Artist",
        album = "Local Album",
        duration = 180
    )

    @Before
    fun setUp() {
        api = mock()
        localDataSource = mock()
        repository = FindTrackRepositoryImpl(api, localDataSource)
    }

    @Test
    fun `should return remote track when id is numeric`() = runTest {
        `when`(api.getTrackById(123L)).thenReturn(apiTrackDto)

        val result = repository.find("123")

        assertTrue(result.isSuccess)
        val track = result.getOrNull()!!
        assertEquals("Remote Song", track.title)
        assertEquals("Remote Artist", track.artist)
        assertEquals(TrackSource.Remote("http://example.com/preview.mp3"), track.source)
    }

    @Test
    fun `should return local track when id is a file path`() = runTest {
        `when`(localDataSource.getLocalTracks()).thenReturn(flowOf(listOf(localTrackDto)))

        val result = repository.find("/music/local_song.mp3")

        assertTrue(result.isSuccess)
        val track = result.getOrNull()!!
        assertEquals("Local Song", track.title)
        assertEquals("Local Artist", track.artist)
        assertEquals(TrackSource.Local("/music/local_song.mp3"), track.source)
    }

    @Test
    fun `should return failure when local track not found`() = runTest {
        `when`(localDataSource.getLocalTracks()).thenReturn(flowOf(emptyList()))

        val result = repository.find("/music/unknown.mp3")

        assertTrue(result.isFailure)
        assertEquals("Трек не найден", result.exceptionOrNull()?.message)
    }

    @Test
    fun `should return failure when API throws exception`() = runTest {
        `when`(api.getTrackById(123L)).thenThrow(RuntimeException("Network error"))

        val result = repository.find("123")

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}