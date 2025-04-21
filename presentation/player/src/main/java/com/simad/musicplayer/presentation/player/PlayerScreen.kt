package com.simad.musicplayer.presentation.player

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.simad.musicplayer.presentation.core.component.MessagePane
import com.simad.musicplayer.presentation.player.PlayerViewModel.Intent
import com.simad.musicplayer.presentation.player.component.PlayerUi
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PlayerScreen(
    trackId: String,
    viewModel: PlayerViewModel
) {
    val state = viewModel.state.collectAsState().value

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isNotificationGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!isNotificationGranted) {
                requestPermissions(context as Activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }

        viewModel.effect.collectLatest { effect ->
            when (effect) {
                PlayerViewModel.Effect.ShowErrorAlbumLoadToast -> Toast.makeText(
                    context,
                    context.getString(com.simad.musicplayer.presentation.core.R.string.album_not_loaded),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    LaunchedEffect(trackId) {
        viewModel.onIntent(Intent.LoadTrack(trackId))
    }

    when {
        state.isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        state.error != null -> MessagePane(
            message = state.error,
            buttonLabel = stringResource(com.simad.musicplayer.presentation.core.R.string.try_again),
            onButtonClick = { viewModel.onIntent(Intent.LoadTrack(trackId)) }
        )

        state.title != null -> PlayerUi(
            title = state.title,
            artist = state.artist,
            album = state.album,
            coverUrl = state.coverUrl,
            isPlaying = state.isPlaying,
            currentPosition = state.currentPosition,
            duration = state.duration,
            onStartRewind = { viewModel.onIntent(Intent.StartRewind) },
            onEndRewind = { viewModel.onIntent(Intent.EndRewind(it)) },
            onPlayPauseClick = { viewModel.onIntent(Intent.PlayPauseClicked) },
            onNextClick = { viewModel.onIntent(Intent.PlayNextTrack) },
            onPreviousClick = { viewModel.onIntent(Intent.PlayPreviousTrack) },
            canPlayNext = state.canPlayNext,
            canPlayPrevious = state.canPlayPrevious,
            modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
        )
    }
}