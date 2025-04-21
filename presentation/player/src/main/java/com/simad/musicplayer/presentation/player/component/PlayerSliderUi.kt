package com.simad.musicplayer.presentation.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simad.musicplayer.presentation.player.util.formatTime

@Composable
fun PlayerSliderUi(
    currentPosition: Long,
    duration: Long,
    onStart: () -> Unit,
    onEnd: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var position by rememberSaveable(currentPosition) { mutableFloatStateOf(currentPosition.toFloat()) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Slider(
            value = position,
            onValueChange = { newValue ->
                onStart()
                position = newValue
            },
            onValueChangeFinished = {
                onEnd(position.toLong())
            },
            valueRange = 0f..duration.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = formatTime(position.toLong()))
            Text(text = formatTime(duration))
        }
    }
}