package com.simad.musicplayer.presentation.player

import android.annotation.SuppressLint
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

@SuppressLint("UnsafeOptInUsageError")
class PlaybackService : MediaSessionService() {

    companion object {
        private const val SESSION_ID = "MUSIC_PLAYER_SESSION_ID"
    }

    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build()

        mediaSession = MediaSession.Builder(this, player!!)
            .setId(SESSION_ID)
            .build()

    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.release()
        player?.release()
        mediaSession = null
        player = null
        super.onDestroy()
    }
}