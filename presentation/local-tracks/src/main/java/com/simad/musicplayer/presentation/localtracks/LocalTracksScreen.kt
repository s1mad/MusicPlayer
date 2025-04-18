package com.simad.musicplayer.presentation.localtracks

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.simad.musicplayer.presentation.core.R
import com.simad.musicplayer.presentation.core.component.MessagePane
import com.simad.musicplayer.presentation.core.component.SearchTrackPanel
import com.simad.musicplayer.presentation.core.component.TrackList
import com.simad.musicplayer.presentation.localtracks.LocalTracksViewModel.Intent.ProcessPermissionResult
import com.simad.musicplayer.presentation.localtracks.util.MediaStoragePermissionChecker
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LocalTracksScreen(
    viewModel: LocalTracksViewModel,
    navToPlayer: (trackId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val shouldShowRationale = (context as? ComponentActivity)
            ?.shouldShowRequestPermissionRationale(permission) == true
        viewModel.onIntent(ProcessPermissionResult(isGranted, shouldShowRationale))
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.onIntent(LocalTracksViewModel.Intent.RetryLoading)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                LocalTracksViewModel.Effect.RequestStoragePermission -> {
                    permissionLauncher.launch(MediaStoragePermissionChecker.MEDIA_STORAGE_PERMISSION)
                }

                LocalTracksViewModel.Effect.NavigateToSettings -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    settingsLauncher.launch(intent)
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            SearchTrackPanel(
                value = state.searchQuery,
                onValueChange = { query ->
                    viewModel.onIntent(LocalTracksViewModel.Intent.UpdateSearchQuery(query))
                },
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
                    onButtonClick = {
                        viewModel.onIntent(LocalTracksViewModel.Intent.RetryLoading)
                    },
                )

                targetState.permissionState == LocalTracksViewModel.PermissionState.Required -> MessagePane(
                    message = stringResource(R.string.app_need_media_permission),
                    buttonLabel = stringResource(R.string.grant_permission),
                    onButtonClick = { viewModel.onIntent(LocalTracksViewModel.Intent.RequestPermission) }
                )

                targetState.permissionState == LocalTracksViewModel.PermissionState.PermanentlyDenied -> MessagePane(
                    message = stringResource(R.string.grant_permission_manually),
                    buttonLabel = stringResource(R.string.open_settings),
                    onButtonClick = { viewModel.onIntent(LocalTracksViewModel.Intent.NavigateToSettings) }
                )

                targetState.tracks.isEmpty() &&
                        targetState.permissionState == LocalTracksViewModel.PermissionState.Granted -> MessagePane(
                    message = stringResource(R.string.no_tracks_found),
                    buttonLabel = stringResource(R.string.try_again),
                    onButtonClick = {
                        viewModel.onIntent(LocalTracksViewModel.Intent.RetryLoading)
                    }
                )

                else -> TrackList(
                    tracks = targetState.tracks,
                    onTrackClick = {
                        navToPlayer(it.id)
                    }
                )
            }
        }
    }
}