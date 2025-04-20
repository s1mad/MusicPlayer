package com.simad.musicplayer.presentation.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.simad.musicplayer.presentation.core.component.TrackCover

@Composable
fun PlayerUi(
    title: String,
    artist: String?,
    album: String?,
    coverUrl: String?,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onStartRewind: () -> Unit,
    onEndRewind: (Long) -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    canPlayNext: Boolean,
    canPlayPrevious: Boolean,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColorFor(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TrackCover(
                url = coverUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.extraLarge)
            )
            PlayerTextUi(
                title = title,
                album = album,
                artist = artist,
                modifier = Modifier.padding(top = 24.dp)
            )
            PlayerSliderUi(
                currentPosition = currentPosition,
                duration = duration,
                onStart = onStartRewind,
                onEnd = onEndRewind,
                modifier = Modifier.padding(top = 24.dp)
            )
            PlayerControlsUi(
                isPlaying = isPlaying,
                onPlayPauseClick = onPlayPauseClick,
                onNextClick = onNextClick,
                onPreviousClick = onPreviousClick,
                canPlayNext = canPlayNext,
                canPlayPrevious = canPlayPrevious,
                modifier = Modifier.padding(top = 24.dp),
            )
        }
    }
}