package com.simad.musicplayer.presentation.player

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.model.TrackSource
import com.simad.musicplayer.domain.model.toUri
import com.simad.musicplayer.domain.usecase.FindAlbumTracksUseCase
import com.simad.musicplayer.domain.usecase.FindTrackUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    private val findTrackUseCase: FindTrackUseCase,
    private val findAlbumTracksUseCase: FindAlbumTracksUseCase,
    private val controllerHandler: MediaControllerHandler
) : ViewModel() {

    data class State(
        val isLoading: Boolean = false,
        val title: String? = null,
        val artist: String? = null,
        val album: String? = null,
        val coverUrl: String? = null,
        val error: String? = null,
        val isPlaying: Boolean = false,
        val currentPosition: Long = 0,
        val duration: Long = 0,
        val isUserRewind: Boolean = false,
        val currentTrackIndex: Int = 0,
        val canPlayNext: Boolean = false,
        val canPlayPrevious: Boolean = true,
    )

    sealed class Intent {
        data class LoadTrack(val trackId: String) : Intent()
        object PlayPauseClicked : Intent()
        object StartRewind : Intent()
        data class EndRewind(val positionMs: Long) : Intent()
        object PlayNextTrack : Intent()
        object PlayPreviousTrack : Intent()
    }

    sealed class Effect {
        data object ShowErrorAlbumLoadToast : Effect()
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>(replay = 1, extraBufferCapacity = 16)
    val effect = _effect.asSharedFlow()

    init {
        setupControllerListener()
        startPositionUpdate()
    }

    private fun setupControllerListener() {
        controllerHandler {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _state.update { it.copy(isPlaying = isPlaying) }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    updateTrackInfo()
                    updateNavigationState()
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    _state.update {
                        it.copy(
                            title = mediaMetadata.title?.toString(),
                            artist = mediaMetadata.artist?.toString(),
                            album = mediaMetadata.albumTitle?.toString(),
                            coverUrl = mediaMetadata.artworkUri?.toString()
                        )
                    }
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    _state.update { it.copy(currentPosition = newPosition.positionMs) }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _state.update { it.copy(duration = duration) }
                    }
                }
            })
        }
    }

    private fun startPositionUpdate() {
        viewModelScope.launch {
            while (isActive) {
                controllerHandler {
                    if (!_state.value.isUserRewind && isPlaying) {
                        _state.update {
                            it.copy(
                                currentPosition = currentPosition,
                                duration = duration
                            )
                        }
                    }
                }
                delay(500)
            }
        }
    }

    private fun updateTrackInfo() {
        controllerHandler {
            val metadata = currentMediaItem?.mediaMetadata
            _state.update {
                it.copy(
                    title = metadata?.title?.toString(),
                    artist = metadata?.artist?.toString(),
                    album = metadata?.albumTitle?.toString(),
                    coverUrl = metadata?.artworkUri?.toString()
                )
            }
        }
    }

    private fun updateNavigationState() {
        controllerHandler {
            val currentIndex = currentMediaItemIndex
            val count = mediaItemCount
            _state.update {
                it.copy(
                    currentTrackIndex = currentIndex,
                    canPlayNext = currentIndex < count - 1,
                    canPlayPrevious = currentIndex > 0
                )
            }
        }
    }

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadTrack -> loadTrack(intent.trackId)
            Intent.PlayPauseClicked -> togglePlayback()
            Intent.StartRewind -> startRewind()
            is Intent.EndRewind -> seekTo(intent.positionMs)
            Intent.PlayNextTrack -> playNextTrack()
            Intent.PlayPreviousTrack -> playPreviousTrack()
        }
    }

    private var loadTrackJob: Job? = null
    private fun loadTrack(trackId: String) {
        loadTrackJob?.cancel()
        loadTrackJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            findTrackUseCase(trackId)
                .onSuccess { track ->
                    preparePlayer(track)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            title = track.title,
                            artist = track.artist,
                            album = track.album,
                            coverUrl = track.coverUrl,
                            duration = track.duration * 1000L
                        )
                    }
                    loadAlbum(track)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private var loadAlbumJob: Job? = null
    private fun loadAlbum(track: Track) {
        loadAlbumJob?.cancel()
        loadAlbumJob = viewModelScope.launch {
            track.albumId?.let { albumId ->
                val isRemoteSource = track.source is TrackSource.Remote
                findAlbumTracksUseCase(albumId, isRemoteSource)
                    .onSuccess { albumTracks ->
                        if (albumTracks.size <= 1) return@onSuccess

                        val currentTrackIndex = albumTracks.indexOfFirst { it.id == track.id }
                        val mediaItems = albumTracks.map { it.toMediaItem() }
                        val before = mediaItems.subList(0, currentTrackIndex)
                        val after = mediaItems.subList(currentTrackIndex + 1, mediaItems.size)
                        controllerHandler {
                            addMediaItems(0, before)
                            addMediaItems(after)
                            updateNavigationState()
                        }
                    }
                    .onFailure {
                        _effect.emit(Effect.ShowErrorAlbumLoadToast)
                    }
            }
        }
    }

    private fun playNextTrack() {
        controllerHandler {
            val currentIndex = currentMediaItemIndex
            val count = mediaItemCount
            if (currentIndex < count - 1) {
                seekToNextMediaItem()
            } else {
                seekTo(0)
            }
        }
    }

    private fun playPreviousTrack() {
        controllerHandler {
            val currentIndex = currentMediaItemIndex
            if (currentIndex > 0) {
                seekToPreviousMediaItem()
            } else {
                seekTo(0)
            }
        }
    }

    private fun preparePlayer(track: Track) {
        controllerHandler {
            setMediaItem(track.toMediaItem())
            prepare()
            setPlayWhenReady(true)
        }
    }

    private fun togglePlayback() {
        controllerHandler {
            if (isPlaying) pause() else play()
        }
    }

    private fun startRewind() {
        _state.update { it.copy(isUserRewind = true) }
    }

    private fun seekTo(ms: Long) {
        controllerHandler {
            _state.update { it.copy(isUserRewind = false) }
            seekTo(ms)
        }
    }
}

private fun Track.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setUri(source.toUri())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setArtworkUri(coverUrl?.toUri())
                .build()
        )
        .build()
}