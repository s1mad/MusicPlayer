package com.simad.musicplayer.presentation.remotetracks

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.simad.musicplayer.presentation.core.component.MessagePane
import com.simad.musicplayer.presentation.core.component.SearchTrackPanel
import com.simad.musicplayer.presentation.core.component.TrackList

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemoteTracksScreen(
    viewModel: RemoteTracksViewModel,
    navToPlayer: (trackId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            SearchTrackPanel(
                value = state.searchQuery,
                onValueChange = { viewModel.onIntent(RemoteTracksViewModel.Intent.UpdateSearchQuery(it)) },
            )
        },
        modifier = modifier
    ) { innerPadding ->
        val paddingValues = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding()
        )

        AnimatedContent(
            targetState = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { targetState ->
            when {
                targetState.isLoading -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator()
                }


                targetState.error != null -> MessagePane(
                    message = targetState.error,
                    buttonLabel = stringResource(R.string.try_again),
                    onButtonClick = { viewModel.onIntent(RemoteTracksViewModel.Intent.RetryLoading) },
                )

                targetState.tracks.isEmpty() -> MessagePane(
                    message = "Треки не найдены",
                    buttonLabel = stringResource(R.string.try_again),
                    onButtonClick = { viewModel.onIntent(RemoteTracksViewModel.Intent.RetryLoading) },
                )

                else -> TrackList(
                    tracks = targetState.tracks,
                    onTrackClick = { track ->
                        navToPlayer(track.id)
                    }
                )
            }
        }
    }
}