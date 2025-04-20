package com.simad.musicplayer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.simad.musicplayer.presentation.localtracks.LocalTracksScreen
import com.simad.musicplayer.presentation.player.PlayerScreen
import com.simad.musicplayer.presentation.remotetracks.RemoteTracksScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppNavDestination.LocalTracks,
        modifier = modifier
    ) {

        composable<AppNavDestination.LocalTracks> {
            LocalTracksScreen(
                viewModel = viewModel(factory = viewModelFactory),
                navToPlayer = { trackId ->
                    navController.navigate(AppNavDestination.Player(trackId))
                }
            )
        }

        composable<AppNavDestination.RemoteTracks> {
            RemoteTracksScreen(
                viewModel = viewModel(factory = viewModelFactory),
                navToPlayer = { trackId ->
                    navController.navigate(AppNavDestination.Player(trackId))
                }
            )
        }

        composable<AppNavDestination.Player> { backStackEntry ->
            val playerRoute: AppNavDestination.Player = backStackEntry.toRoute()
            PlayerScreen(
                trackId = playerRoute.trackId,
                viewModel = viewModel(factory = viewModelFactory),
            )
        }
    }
}