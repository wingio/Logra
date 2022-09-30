package xyz.wingio.logra.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import xyz.wingio.logra.crashdetector.services.CrashDetectionService
import kotlin.concurrent.thread

var isCrashServiceActive = false

val Context.hasLogsPermission: Boolean
    get() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_LOGS
    ) == PackageManager.PERMISSION_GRANTED

val Context.hasNotificationPermission: Boolean
    get() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

val Context.crashServiceIntent: Intent
    get() = Intent(this, CrashDetectionService::class.java)

fun Context.initCrashService() {
    if (!hasNotificationPermission && this is Activity) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
    }
    if (!isCrashServiceActive) {
        thread(start = true, name = "CrashDetectorStarter") {
            startCrashService()
        }

        isCrashServiceActive = true
    }
}

fun Context.startCrashService() {
    if (!hasLogsPermission)
        return

    ContextCompat.startForegroundService(this@Context, crashServiceIntent)
}

fun Context.stopCrashService() {
    if (stopService(crashServiceIntent))
        isCrashServiceActive = false
}
