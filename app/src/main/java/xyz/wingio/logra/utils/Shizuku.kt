package xyz.wingio.logra.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import xyz.wingio.logra.BuildConfig
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// Must be a callback because the Shizuku permission request callback is run on the main thread,
// and can't be called if the main thread is blocked
suspend fun checkShizukuPermission() = suspendCoroutine<ShizukuRequestResult> {
    if (Build.VERSION.SDK_INT < 23) {
        it.resume(ShizukuRequestResult.OUTDATED_SDK)
        return@suspendCoroutine
    }
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
        val shizukuRequestCode = (0..5).shuffled().joinToString("").toInt()
        // Set a listener for the permission grant/deny
        Shizuku.addRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode != shizukuRequestCode) return@addRequestPermissionResultListener
            it.resume(
                if (grantResult == PackageManager.PERMISSION_GRANTED) ShizukuRequestResult.GRANTED
                else ShizukuRequestResult.DENIED
            )
        }
        // Request the permission
        Shizuku.requestPermission(shizukuRequestCode)
    }
}

suspend fun grantPermissionsWithShizuku() = withContext(Dispatchers.IO) {
    if (Build.VERSION.SDK_INT < 23) return@withContext false
    if (!Shizuku.pingBinder()) return@withContext false
    @Suppress("DEPRECATION")
    val process = Shizuku.newProcess(arrayOf("sh"), null, null)
    process.outputStream.write(
        ("pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.READ_LOGS}" +
                " && am start -n ${BuildConfig.APPLICATION_ID}/${BuildConfig.APPLICATION_ID}.MainActivity" +
                " && exit\n").toByteArray()
    )
    process.outputStream.flush()
    process.waitFor()
}

enum class ShizukuRequestResult {
    GRANTED,
    DENIED,
    PRE_V11,
    NOT_RUNNING,
    OUTDATED_SDK
}