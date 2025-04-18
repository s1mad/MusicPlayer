package com.simad.musicplayer.presentation.localtracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.usecase.GetLocalTracksUseCase
import com.simad.musicplayer.domain.usecase.SearchLocalTracksUseCase
import com.simad.musicplayer.presentation.localtracks.util.MediaStoragePermissionChecker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocalTracksViewModel @Inject constructor(
    private val getLocalTracksUseCase: GetLocalTracksUseCase,
    private val searchLocalTracksUseCase: SearchLocalTracksUseCase,
    private val permissionChecker: MediaStoragePermissionChecker,
) : ViewModel() {

    data class State(
        val tracks: List<Track> = emptyList(),
        val isLoading: Boolean = false,
        val searchQuery: String = "",
        val error: String? = null,
        val permissionState: PermissionState = PermissionState.Initial
    )

    sealed class PermissionState {
        object Initial : PermissionState()
        object Required : PermissionState()
        object PermanentlyDenied : PermissionState()
        object Granted : PermissionState()
    }

    sealed class Intent {
        data class UpdateSearchQuery(val query: String) : Intent()
        object RetryLoading : Intent()
        object RequestPermission : Intent()
        object NavigateToSettings : Intent()
        class ProcessPermissionResult(val isGranted: Boolean, val shouldShowRationale: Boolean) : Intent()
    }

    sealed class Effect {
        object RequestStoragePermission : Effect()
        object NavigateToSettings : Effect()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> get() = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>(replay = 1, extraBufferCapacity = 16)
    val effect = _effect.asSharedFlow()

    private var loadOrSearchJob: Job? = null

    init {
        loadOrSearchTracksWithPermissionCheck()
    }

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.UpdateSearchQuery -> updateSearchQuery(intent.query)
            is Intent.RetryLoading -> loadOrSearchTracks(_state.value.searchQuery)
            is Intent.RequestPermission -> requestPermission()
            is Intent.NavigateToSettings -> navigateToSettings()
            is Intent.ProcessPermissionResult -> processPermissionResult(intent.isGranted, intent.shouldShowRationale)
        }
    }

    private fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query, error = null) }
        loadOrSearchTracks(query)
    }

    private fun loadOrSearchTracksWithPermissionCheck() {
        when (permissionChecker.checkStoragePermission()) {
            MediaStoragePermissionChecker.PermissionCheckResult.GRANTED -> {
                _state.update { it.copy(permissionState = PermissionState.Granted) }
                loadOrSearchTracks(_state.value.searchQuery)
            }

            MediaStoragePermissionChecker.PermissionCheckResult.DENIED -> {
                _state.update {
                    it.copy(permissionState = PermissionState.Required)
                }
            }

            MediaStoragePermissionChecker.PermissionCheckResult.DENIED_PERMANENTLY -> {
                _state.update {
                    it.copy(permissionState = PermissionState.PermanentlyDenied)
                }
            }
        }
    }

    private fun loadOrSearchTracks(query: String) {
        if (_state.value.permissionState != PermissionState.Granted) {
            loadOrSearchTracksWithPermissionCheck()
            return
        }

        loadOrSearchJob?.cancel()
        _state.update { it.copy(isLoading = true, error = null) }

        loadOrSearchJob = viewModelScope.launch {
            val flow = if (query.isEmpty()) getLocalTracksUseCase() else searchLocalTracksUseCase(query)

            flow.collect { result ->
                result.fold(
                    onSuccess = { tracks ->
                        _state.update {
                            it.copy(
                                tracks = tracks,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { e ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                tracks = emptyList(),
                                error = e.message ?: "Failed to load tracks"
                            )
                        }
                    }
                )
            }
        }
    }

    private fun requestPermission() {
        viewModelScope.launch {
            _effect.emit(Effect.RequestStoragePermission)
        }
    }

    private fun navigateToSettings() {
        viewModelScope.launch {
            _effect.emit(Effect.NavigateToSettings)
        }
    }

    private fun processPermissionResult(isGranted: Boolean, shouldShowRationale: Boolean) {
        val newPermissionState = when (
            permissionChecker.resolvePermissionState(isGranted, shouldShowRationale)
        ) {
            MediaStoragePermissionChecker.PermissionCheckResult.GRANTED -> PermissionState.Granted
            MediaStoragePermissionChecker.PermissionCheckResult.DENIED_PERMANENTLY -> PermissionState.PermanentlyDenied
            MediaStoragePermissionChecker.PermissionCheckResult.DENIED -> PermissionState.Required
        }

        _state.update {
            it.copy(permissionState = newPermissionState)
        }

        if (newPermissionState == PermissionState.Granted) {
            updateSearchQuery(_state.value.searchQuery)
        }
    }
}