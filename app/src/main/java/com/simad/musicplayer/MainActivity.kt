package com.simad.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.simad.musicplayer.navigation.AppNavDestination
import com.simad.musicplayer.navigation.AppNavGraph
import com.simad.musicplayer.presentation.core.theme.MusicPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }
}

@Composable
private fun App() = MusicPlayerTheme {
    val navController = rememberNavController()

    val color = MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColorFor(color),
        ) {
            AppNavGraph(navController = navController, modifier = Modifier.weight(1f))
            AppNavigationBar(navController = navController)
        }
    }
}

@Composable
private fun AppNavigationBar(
    navController: NavHostController
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val visibleOnDestinations = AppNavDestination.NavigationBarVisibleOnDestinations.map { it::class.qualifiedName }

    AnimatedVisibility(
        visible = currentRoute in visibleOnDestinations,
        enter = slideInVertically(
            initialOffsetY = { it }
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it }
        ) + fadeOut()
    ) {
        NavigationBar {
            NavigationBarItem(
                selected = currentRoute == AppNavDestination.LocalTracks::class.qualifiedName,
                onClick = { navController.navigate(AppNavDestination.LocalTracks) },
                icon = { Icon(Icons.Rounded.Favorite, contentDescription = "LocalTracks") },
                label = { Text(text = stringResource(R.string.local_navigation_bar_item)) }
            )
            NavigationBarItem(
                selected = currentRoute == AppNavDestination.RemoteTracks::class.qualifiedName,
                onClick = { navController.navigate(AppNavDestination.RemoteTracks) },
                icon = { Icon(Icons.Rounded.Search, contentDescription = "RemoteTracks") },
                label = { Text(text = stringResource(R.string.remote_navigation_bar_item)) }
            )
        }
    }
}