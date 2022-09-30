package xyz.wingio.logra.crashdetector.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import xyz.wingio.logra.R
import xyz.wingio.logra.crashdetector.receivers.StopReceiver
import xyz.wingio.logra.utils.Channels
import xyz.wingio.logra.utils.detector.CrashDetector

class CrashDetectionService : Service() {

    private lateinit var detector: CrashDetector

    override fun onCreate() {
        showNotification()
        detector = CrashDetector(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.kill()
    }

    override fun onBind(i: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    private fun showNotification() {
        val stopAction = NotificationCompat.Action(
            0, getString(R.string.stop), PendingIntent.getBroadcast(
                this,
                0,
                Intent(this, StopReceiver::class.java),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                else
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        val msg = resources.getStringArray(R.array.crash_service_messages).random()

        startForeground(
            298295889720770563.toInt(),
            NotificationCompat.Builder(this, Channels.CRASH_DETECTOR_STATUS)
                .setSmallIcon(R.drawable.ic_bug_24)
                .setContentTitle(msg)
                .setForegroundServiceBehavior(NotificationCompat.FLAG_FOREGROUND_SERVICE)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .addAction(stopAction)
                .build()
        )
    }

}