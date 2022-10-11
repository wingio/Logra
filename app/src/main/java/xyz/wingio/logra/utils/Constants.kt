package xyz.wingio.logra.utils

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import xyz.wingio.logra.R
import xyz.wingio.logra.domain.logcat.LogLevel
import xyz.wingio.logra.domain.logcat.LogcatEntry
import java.util.*

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

val DEMO_LOG
    @Composable get() = LogcatEntry(
        Date(),
        0,
        LogLevel.WARNING,
        stringResource(id = R.string.app_name),
        content = stringResource(id = R.string.demo_log_text),
        raw = ""
    )