package xyz.wingio.logra.utils

import android.Manifest
import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import xyz.wingio.logra.BuildConfig
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

// Must be a callback because the Shizuku permission request callback is run on the main thread,
// and can't be called if the main thread is blocked
suspend fun checkShizukuPermission() = suspendCoroutine<ShizukuRequestResult> {
    if (!Shizuku.pingBinder()) {
        it.resume(ShizukuRequestResult.NOT_RUNNING)
        return@suspendCoroutine
    }
    if (Shizuku.isPreV11()) {
        it.resume(ShizukuRequestResult.PRE_V11)
        return@suspendCoroutine
    }

    if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
        // Granted
        it.resume(ShizukuRequestResult.GRANTED)
    } else if (Shizuku.shouldShowRequestPermissionRationale()) {
        // Users choose "Deny and don't ask again"
        it.resume(ShizukuRequestResult.DENIED)
    } else {
        // Obtain a random permission request code
        val shizukuRequestCode = (0..9).shuffled().joinToString("").toInt()
        // Set a listener for the permission grant/deny
        Shizuku.addRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode != shizukuRequestCode) return@addRequestPermissionResultListener
            Logger("dn").debug(grantResult.toString())
            it.resume(
                if (grantResult == PackageManager.PERMISSION_GRANTED) ShizukuRequestResult.GRANTED
                else ShizukuRequestResult.DENIED
            )
        }
        // Request the permission
        Shizuku.requestPermission(shizukuRequestCode)
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

enum class ShizukuRequestResult {
    GRANTED,
    DENIED,
    PRE_V11,
    NOT_RUNNING
}