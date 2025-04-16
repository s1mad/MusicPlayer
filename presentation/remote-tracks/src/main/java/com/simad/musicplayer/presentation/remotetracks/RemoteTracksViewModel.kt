package com.simad.musicplayer.presentation.remotetracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.usecase.GetRemoteChartTracksUseCase
import com.simad.musicplayer.domain.usecase.SearchRemoteTracksUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class RemoteTracksViewModel @Inject constructor(
    private val getRemoteChartTracksUseCase: GetRemoteChartTracksUseCase,
    private val searchRemoteTracksUseCase: SearchRemoteTracksUseCase
) : ViewModel() {

    data class State(
        val tracks: List<Track> = emptyList(),
        val isLoading: Boolean = false,
        val searchQuery: String = "",
        val error: String? = null
    )

    sealed class Intent {
        data class UpdateSearchQuery(val query: String) : Intent()
        object RetryLoading : Intent()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> get() = _state.asStateFlow()

    private var loadOrSearchJob: Job? = null

    init {
        loadOrSearchTracks()
    }

    fun onIntent(intent: Intent) {
        when (intent) {
            Intent.RetryLoading -> loadOrSearchTracks()
            is Intent.UpdateSearchQuery -> onSearchQueryChange(intent.query)
        }
    }

    private fun onSearchQueryChange(query: String) {
        _state.update { current ->
            current.copy(searchQuery = query, error = null)
        }

        loadOrSearchTracks()
    }

    private fun loadOrSearchTracks() {

        loadOrSearchJob?.cancel()
        _state.update { it.copy(isLoading = true, error = null) }

        loadOrSearchJob = viewModelScope.launch {
            val query: String = _state.value.searchQuery
            val flow = if (query.isEmpty()) getRemoteChartTracksUseCase() else searchRemoteTracksUseCase(query)

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
                                error = e.message ?: "Не удалось загрузить треки"
                            )
                        }
                    }
                )
            }
        }
    }
}