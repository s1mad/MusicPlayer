package com.simad.musicplayer.presentation.player.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simad.musicplayer.presentation.core.R

@Composable
fun PlayerControlsUi(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    canPlayNext: Boolean,
    canPlayPrevious: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        PlayerControlButton(
            imageVector = Icons.Rounded.SkipPrevious,
            contentDescription = stringResource(R.string.previous_track),
            onClick = onPreviousClick,
            enabled = canPlayPrevious,
            size = 64.dp
        )
        PlayerControlButton(
            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = if (isPlaying) stringResource(R.string.pause) else stringResource(R.string.play),
            onClick = onPlayPauseClick,
        )
        PlayerControlButton(
            imageVector = Icons.Rounded.SkipNext,
            contentDescription = stringResource(R.string.next_track),
            onClick = onNextClick,
            enabled = canPlayNext,
            size = 64.dp
        )
    }
}

@Composable
private fun PlayerControlButton(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: Dp = 72.dp,
    modifier: Modifier = Modifier
) {
    val tint = if (enabled) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.32f)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(
                onClick = onClick,
                role = Role.Button,
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = false,
                    radius = size / 2
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = imageVector
        ) {
            Icon(
                imageVector = it,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.fillMaxSize(0.68f)
            )
        }
    }
}