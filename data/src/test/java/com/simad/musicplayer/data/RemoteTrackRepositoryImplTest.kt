package com.simad.musicplayer.data

import app.cash.turbine.test
import com.simad.musicplayer.data.remote.DeezerApi
import com.simad.musicplayer.data.remote.dto.ApiAlbumDto
import com.simad.musicplayer.data.remote.dto.ApiArtistDto
import com.simad.musicplayer.data.remote.dto.ApiTrackDto
import com.simad.musicplayer.data.remote.dto.ApiTracksResponse
import com.simad.musicplayer.data.repository.RemoteTrackRepositoryImpl
import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.model.TrackSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteTrackRepositoryImplTest {

    private lateinit var api: DeezerApi
    private lateinit var repository: RemoteTrackRepositoryImpl

    private val testApiTrackDto = ApiTrackDto(
        id = 1L,
        title = "Test Song",
        artist = ApiArtistDto(name = "Test Artist"),
        album = ApiAlbumDto(title = "Test Album", cover = "http://example.com/cover.jpg"),
        preview = "http://example.com/preview.mp3",
        duration = 180
    )

    private val expectedTrack = Track(
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
        api = mock()
        repository = RemoteTrackRepositoryImpl(api)
    }

    @Test
    fun `chartTracks should emit success with mapped tracks when API call succeeds`() = runTest {

        val apiResponse = ApiTracksResponse(data = listOf(testApiTrackDto))
        `when`(api.getChartTracks()).thenReturn(apiResponse)


        repository.chartTracks().test {
            val result = awaitItem()
            assertTrue(result.isSuccess, "Expected success but got $result")
            assertEquals(listOf(expectedTrack), result.getOrNull(), "Tracks do not match")
            awaitComplete()
        }
    }

    @Test
    fun `chartTracks should emit failure when API throws IOException`() = runTest {

        val ioException = IOException("Network failure")
        `when`(api.getChartTracks()).thenAnswer { throw ioException }


        repository.chartTracks().test {
            val result = awaitItem()
            assertTrue(result.isFailure, "Expected failure but got $result")
            val exception = result.exceptionOrNull()
            assertTrue(exception is IOException, "Expected IOException but got ${exception?.javaClass}")
            assertEquals("Network error: Network failure", exception?.message, "Exception message mismatch")
            awaitComplete()
        }
    }

    @Test
    fun `chartTracks should emit failure when API throws unexpected Exception`() = runTest {

        val exception = RuntimeException("Unexpected error")
        `when`(api.getChartTracks()).thenAnswer { throw exception }


        repository.chartTracks().test {
            val result = awaitItem()
            assertTrue(result.isFailure, "Expected failure but got $result")
            val exception = result.exceptionOrNull()
            assertTrue(exception is Exception, "Expected Exception but got ${exception?.javaClass}")
            assertEquals("Unexpected error: Unexpected error", exception?.message, "Exception message mismatch")
            awaitComplete()
        }
    }

    @Test
    fun `searchTracks should emit success with mapped tracks when API call succeeds`() = runTest {

        val query = "test"
        val apiResponse = ApiTracksResponse(data = listOf(testApiTrackDto))
        `when`(api.searchTracks(query)).thenReturn(apiResponse)


        repository.searchTracks(query).test {
            val result = awaitItem()
            assertTrue(result.isSuccess, "Expected success but got $result")
            assertEquals(listOf(expectedTrack), result.getOrNull(), "Tracks do not match")
            awaitComplete()
        }
    }

    @Test
    fun `searchTracks should emit failure when API throws IOException`() = runTest {

        val query = "test"
        val ioException = IOException("Network failure")
        `when`(api.searchTracks(query)).thenAnswer { throw ioException }


        repository.searchTracks(query).test {
            val result = awaitItem()
            assertTrue(result.isFailure, "Expected failure but got $result")
            val exception = result.exceptionOrNull()
            assertTrue(exception is IOException, "Expected IOException but got ${exception?.javaClass}")
            assertEquals("Network error: Network failure", exception?.message, "Exception message mismatch")
            awaitComplete()
        }
    }

    @Test
    fun `searchTracks should emit failure when API throws unexpected Exception`() = runTest {

        val query = "test"
        val exception = RuntimeException("Unexpected error")
        `when`(api.searchTracks(query)).thenAnswer { throw exception }


        repository.searchTracks(query).test {
            val result = awaitItem()
            assertTrue(result.isFailure, "Expected failure but got $result")
            val exception = result.exceptionOrNull()
            assertTrue(exception is Exception, "Expected Exception but got ${exception?.javaClass}")
            assertEquals("Unexpected error: Unexpected error", exception?.message, "Exception message mismatch")
            awaitComplete()
        }
    }
}