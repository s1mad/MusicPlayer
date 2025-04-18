package com.simad.musicplayer.data.local

import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import com.simad.musicplayer.data.local.dto.LocalTrackDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class LocalTrackDataSource @Inject constructor(
    private val context: Context
) {
    fun getLocalTracks(): Flow<List<LocalTrackDto>> = flow {
        val tracks = mutableListOf<LocalTrackDto>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )
        val selection = "${MediaStore.Audio.Media.MIME_TYPE} = ?"
        val selectionArgs = arrayOf("audio/mpeg")

        context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            while (cursor.moveToNext()) {
                val filePath = cursor.getString(dataIndex)
                val file = File(filePath)
                if (file.exists()) {
                    tracks.add(file.toLocalTrackDto())
                }
            }
        }
        emit(tracks)
    }

    fun searchLocalTracks(query: String): Flow<List<LocalTrackDto>> = flow {
        val tracks = mutableListOf<LocalTrackDto>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )
        val selection = "${MediaStore.Audio.Media.MIME_TYPE} = ? AND (" +
                "${MediaStore.Audio.Media.TITLE} LIKE ? OR " +
                "${MediaStore.Audio.Media.ARTIST} LIKE ? OR " +
                "${MediaStore.Audio.Media.ALBUM} LIKE ?)"
        val selectionArgs = arrayOf(
            "audio/mpeg",
            "%$query%",
            "%$query%",
            "%$query%"
        )

        context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            while (cursor.moveToNext()) {
                val filePath = cursor.getString(dataIndex)
                val file = File(filePath)
                if (file.exists()) {
                    tracks.add(file.toLocalTrackDto())
                }
            }
        }
        emit(tracks)
    }

    private fun File.toLocalTrackDto(): LocalTrackDto {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(absolutePath)
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: nameWithoutExtension
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown"
            val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
            val duration = (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0) / 1000
            val coverPath = retriever.embeddedPicture?.let { bytes ->
                val coverFile = File(context.cacheDir, "${nameWithoutExtension}.jpg")
                coverFile.writeBytes(bytes)
                coverFile.absolutePath
            }
            return LocalTrackDto(
                filePath = absolutePath,
                title = title,
                artist = artist,
                album = album,
                duration = duration,
                coverPath = coverPath
            )
        } finally {
            retriever.release()
        }
    }
}