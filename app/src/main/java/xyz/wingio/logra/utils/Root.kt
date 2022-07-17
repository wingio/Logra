package xyz.wingio.logra.utils

import android.Manifest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.wingio.logra.BuildConfig

/**
 * Uses root to grant the app [Manifest.permission.READ_LOGS] permission.
 */
suspend fun grantPermissionsWithRoot() = withContext(Dispatchers.IO) {
    val process = Runtime
        .getRuntime()
        .exec("su -c pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.READ_LOGS}")
        .apply { waitFor() }

    if (process.exitValue() != 0) {
        throw Error("Failed to grant log permission with root! Error ${process.exitValue()}")
    }
}
