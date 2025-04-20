package com.simad.musicplayer.presentation.core.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.simad.musicplayer.domain.model.Track
import com.simad.musicplayer.domain.model.TrackSource
import com.simad.musicplayer.presentation.core.theme.MusicPlayerTheme

@Composable
fun TrackList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items = tracks, key = { it.id }) { track ->
            TrackItem(track = track, onClick = { onTrackClick(track) }, modifier = Modifier.animateItem())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackItemPreview() = MusicPlayerTheme {
    val tracks = List(10) {
        Track(
            id = it.toString(),
            title = "title",
            artist = "artist",
            album = "album",
            albumId = "",
            coverUrl = null,
            duration = 100,
            source = TrackSource.Remote("")
        )
    }

    TrackList(
        tracks = tracks,
        onTrackClick = {},
    )
}