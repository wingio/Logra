package xyz.wingio.logra.crashdetector.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import xyz.wingio.logra.utils.stopCrashService

class StopReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        context.stopCrashService()
    }

}