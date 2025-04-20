package com.simad.musicplayer.presentation.player

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import javax.inject.Inject

class MediaControllerHandler @Inject constructor(
    private val context: Context
) {
    private val futureController = MediaController.Builder(
        context,
        SessionToken(context, ComponentName(context, PlaybackService::class.java))
    ).buildAsync()

    operator fun invoke(block: MediaController.() -> Unit) {
        futureController.addListener(
            { futureController.get().apply { block() } },
            ContextCompat.getMainExecutor(context)
        )
    }
}