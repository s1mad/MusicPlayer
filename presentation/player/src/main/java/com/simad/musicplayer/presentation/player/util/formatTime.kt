package com.simad.musicplayer.presentation.player.util

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatTime(millis: Long): String {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%2d:%02d", minutes, remainingSeconds)
}