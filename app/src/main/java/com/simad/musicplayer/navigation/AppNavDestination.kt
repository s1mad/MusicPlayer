package com.simad.musicplayer.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavDestination {

    @Serializable
    data object LocalTracks : AppNavDestination

    @Serializable
    data object RemoteTracks : AppNavDestination

    @Serializable
    data class Player(val trackId: String) : AppNavDestination
}