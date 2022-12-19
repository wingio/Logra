package xyz.wingio.logra.crashdetector.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.utils.initCrashService

class BootReceiver : BroadcastReceiver(), KoinComponent {

    val prefs: PreferenceManager by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        if(prefs.crashDetectorEnabled) context.initCrashService()
    }

}