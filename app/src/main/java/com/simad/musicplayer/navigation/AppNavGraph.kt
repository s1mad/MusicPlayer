package com.simad.musicplayer.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun AppNavGraph(
    navController: NavHostController,
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
            Text("RemoteTracks")
        }

        composable<AppNavDestination.Player> { backStackEntry ->
            val playerRoute: AppNavDestination.Player = backStackEntry.toRoute()
            Text("Player (trackId = ${playerRoute.trackId})")
        }
    }
}