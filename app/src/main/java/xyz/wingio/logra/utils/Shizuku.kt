package xyz.wingio.logra.utils

import android.Manifest
import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import xyz.wingio.logra.BuildConfig
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// Random code to pass to Shizuku
const val PERMISSION_REQUEST_CODE = 4652

// Must be a callback because the Shizuku permission request callback is run on the main thread,
// and can't be called if the main thread is blocked
suspend fun checkShizukuPermission() = suspendCoroutine<Boolean> {
    if (!Shizuku.pingBinder() || Shizuku.isPreV11()) {
        // Pre-v11 is unsupported, and no binder means the Shizuku service isn't running
        it.resume(false)
        return@suspendCoroutine
    }

    if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
        // Granted
        it.resume(true)
    } else if (Shizuku.shouldShowRequestPermissionRationale()) {
        // Users choose "Deny and don't ask again"
        it.resume(false)
    } else {
        // Set a listener for the permission grant/deny
        lateinit var permissionResultListener: (Int, Int) -> Unit
        permissionResultListener = permissionResultListener@ { requestCode, grantResult ->
            if (requestCode != PERMISSION_REQUEST_CODE) return@permissionResultListener
            Logger("dn").debug(grantResult.toString())
            it.resume(grantResult == PackageManager.PERMISSION_GRANTED)
            Shizuku.removeRequestPermissionResultListener(permissionResultListener)
        }
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
        // Request the permission
        Shizuku.requestPermission(PERMISSION_REQUEST_CODE)
    }
}

fun grantPermissionsWithShizuku(): Boolean {
    if (!Shizuku.pingBinder()) return false
    val command = arrayOf("pm", "grant", BuildConfig.APPLICATION_ID, Manifest.permission.READ_LOGS)
    @Suppress("DEPRECATION")
    val process = Shizuku.newProcess(command, null, null)
    val result = process.waitFor()
    return result == 0
}