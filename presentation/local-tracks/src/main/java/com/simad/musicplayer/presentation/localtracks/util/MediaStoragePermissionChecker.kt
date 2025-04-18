package com.simad.musicplayer.presentation.localtracks.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import javax.inject.Inject

class MediaStoragePermissionChecker @Inject constructor(private val context: Context) {

    companion object {
        val MEDIA_STORAGE_PERMISSION: String
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
    }

    enum class PermissionCheckResult {
        GRANTED,
        DENIED,
        DENIED_PERMANENTLY
    }

    fun checkStoragePermission(): PermissionCheckResult {
        return when {
            ContextCompat.checkSelfPermission(
                context,
                MEDIA_STORAGE_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED -> {
                PermissionCheckResult.GRANTED
            }

            (context as? ComponentActivity)
                ?.shouldShowRequestPermissionRationale(MEDIA_STORAGE_PERMISSION) == true -> {
                PermissionCheckResult.DENIED_PERMANENTLY
            }

            else -> {
                PermissionCheckResult.DENIED
            }
        }
    }

    fun resolvePermissionState(isGranted: Boolean, shouldShowRationale: Boolean): PermissionCheckResult {
        return when {
            isGranted -> PermissionCheckResult.GRANTED
            !shouldShowRationale -> PermissionCheckResult.DENIED_PERMANENTLY
            else -> PermissionCheckResult.DENIED
        }
    }
}