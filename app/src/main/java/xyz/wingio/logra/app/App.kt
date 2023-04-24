package xyz.wingio.logra.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Looper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import xyz.wingio.logra.di.databaseModule
import xyz.wingio.logra.di.managerModule
import xyz.wingio.logra.di.viewModelModule
import xyz.wingio.logra.utils.Channels
import xyz.wingio.logra.utils.Logger
import kotlin.system.exitProcess


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        setUpInternalCrashDetector()
        setUpNotificationChannels()

        startKoin {

            androidContext(this@App)

            modules(
                databaseModule(),
                viewModelModule(),
                managerModule(),
            )

        }

    }

    private fun setUpNotificationChannels() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        NotificationChannel(
            Channels.CRASH_DETECTOR,
            "Crash Detector",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Listens for any app crashes and gives you the stacktrace"
            nm.createNotificationChannel(this)
        }

        NotificationChannel(
            Channels.CRASH_DETECTOR_STATUS,
            "Crash Detector Status",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Status for the crash detector"
            setShowBadge(false)
            nm.createNotificationChannel(this)
        }
    }

    private fun setUpInternalCrashDetector() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Logger.default.error("Error on thread ${thread.name}", throwable)
            if (thread == Looper.getMainLooper().thread) exitProcess(1)
        }
    }

}