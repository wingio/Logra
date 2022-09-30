package xyz.wingio.logra.utils

import android.os.SystemClock

val BOOT_TIME = System.currentTimeMillis() - SystemClock.uptimeMillis()

object Intents {

    object Actions {
        const val EXCEPTION = "logra.actions.EXCEPTION"
        const val VIEW_CRASH = "logra.actions.VIEW_CRASH"
    }

    object Extras {
        const val PACKAGE_NAME = "logra.extras.PACKAGE_NAME"
        const val TIME = "logra.extras.TIME"
        const val DESCRIPTION = "logra.extras.DESCRIPTION"
        const val STACKTRACE = "logra.extras.STACKTRACE"
        const val CRASH = "logra.extras.CRASH"
    }

}

object Channels {
    const val CRASH_DETECTOR = "logra.channels.CRASH_DETECTOR"
    const val CRASH_DETECTOR_STATUS = "logra.channels.CRASH_DETECTOR_STATUS"
}