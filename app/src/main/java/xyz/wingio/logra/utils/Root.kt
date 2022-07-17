package xyz.wingio.logra.utils

import android.Manifest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.wingio.logra.BuildConfig
import java.io.File
import java.io.IOException

/**
 * Checks if the phone has granted the app root access
 */
suspend fun checkRootPermission() = withContext(Dispatchers.IO) {
    try {
        val process = Runtime.getRuntime().exec("su -c 'echo'")
        val exitCode = process.waitFor()
        exitCode == 0
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

/**
 * Uses root to grant the app [Manifest.permission.READ_LOGS] permission.
 */
suspend fun grantPermissionsWithRoot() = withContext(Dispatchers.IO) {
    val process = Runtime.getRuntime().exec("su -c 'pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.READ_LOGS}'")
    val exitCode = process.waitFor()
    return@withContext exitCode == 0
}