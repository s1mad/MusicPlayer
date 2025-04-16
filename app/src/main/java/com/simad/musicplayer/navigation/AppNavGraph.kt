package com.simad.musicplayer.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
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
            Text("LocalTracks")
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
            Text("Player (trackId = ${playerRoute.trackId})")
        }
    }
}