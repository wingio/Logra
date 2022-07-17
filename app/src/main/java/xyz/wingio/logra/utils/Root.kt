package xyz.wingio.logra.utils

import android.Manifest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.wingio.logra.BuildConfig
import java.io.File
import java.io.IOException

/**
 * Uses root to grant the app [Manifest.permission.READ_LOGS] permission.
 */
suspend fun grantPermissionsWithRoot() = withContext(Dispatchers.IO) {
    val process = Runtime.getRuntime().exec(
        "su -c 'pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.READ_LOGS} && am force-stop ${BuildConfig.APPLICATION_ID} && am start -n ${BuildConfig.APPLICATION_ID}/${BuildConfig.APPLICATION_ID}.MainActivity'"
    )
    val exitCode = process.waitFor()
    return@withContext exitCode == 0
}