package xyz.wingio.logra.utils

import android.Manifest
import android.app.ActivityManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Parcel
import moe.shizuku.server.IShizukuService
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuRemoteProcess
import xyz.wingio.logra.BuildConfig
import java.lang.reflect.Method
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// Random code to pass to Shizuku
const val PERMISSION_REQUEST_CODE = 8392

// Reflection methods
val getCurrentUserMethod: Method =
    ActivityManager::class.java.getMethod("getCurrentUser")

// This has to be a lateinit var as kotlin does not allow referencing lambdas inside themselves, making it impossible to remove the lambda when called
lateinit var shizukuPermissionCallback: (Int, Int) -> Unit

suspend fun checkShizukuPermission() = suspendCoroutine<Boolean> {
    if (!Shizuku.pingBinder() || Shizuku.isPreV11()) {
        // Pre-v11 is unsupported, and no bider means the shizuku service isn't running
        it.resume(false)
    }

    if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
        // Granted
        it.resume(true)
    } else if (Shizuku.shouldShowRequestPermissionRationale()) {
        // Users choose "Deny and don't ask again"
        it.resume(false)
    } else {
        // Set a listener for the permission grant/deny
        shizukuPermissionCallback = callback@{ requestCode, grantResult ->
            if (requestCode != PERMISSION_REQUEST_CODE) return@callback
            Shizuku.removeRequestPermissionResultListener(shizukuPermissionCallback)
            it.resume(
                grantResult == PackageInfo.REQUESTED_PERMISSION_GRANTED
            )
        }
        Shizuku.addRequestPermissionResultListener(shizukuPermissionCallback)
        // Request the permission
        Shizuku.requestPermission(PERMISSION_REQUEST_CODE)
    }
}

fun grantPermissionWithShizuku(): Boolean {
    if (!Shizuku.pingBinder()) return false
    val command = arrayOf("pm", "grant", BuildConfig.APPLICATION_ID, Manifest.permission.READ_LOGS)
    val process = Shizuku.newProcess(command, null, null)
    val result = process.waitFor()
    return result == 0
}