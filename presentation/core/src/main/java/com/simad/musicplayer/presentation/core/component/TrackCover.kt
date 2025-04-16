package com.simad.musicplayer.presentation.core.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.simad.musicplayer.presentation.core.theme.MusicPlayerTheme

@Composable
fun TrackCover(
    url: String?,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = "Track cover",
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        loading = {
            val color = MaterialTheme.colorScheme.surfaceContainer

            val alpha by rememberInfiniteTransition(label = "loadingPulse").animateFloat(
                initialValue = 0.6f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color.copy(alpha = alpha))
            )
        },
        error = {
            val color = MaterialTheme.colorScheme.surfaceContainer
            Box(
                modifier = Modifier.background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = "Default track caver",
                    tint = contentColorFor(color),
                    modifier = Modifier.fillMaxSize(0.5f)
                )
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun TrackCoverPreview() = MusicPlayerTheme {
    TrackCover(
        url = null,
        modifier = Modifier
            .size(72.dp)
            .clip(MaterialTheme.shapes.large)
    )
}