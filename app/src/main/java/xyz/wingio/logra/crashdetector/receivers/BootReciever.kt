package xyz.wingio.logra.crashdetector.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import xyz.wingio.logra.utils.initCrashService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        context.initCrashService()
    }

}