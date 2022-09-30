package xyz.wingio.logra.crashdetector.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.wingio.logra.MainActivity
import xyz.wingio.logra.R
import xyz.wingio.logra.crashdetector.db.CrashesDatabase
import xyz.wingio.logra.crashdetector.db.entities.Crash
import xyz.wingio.logra.utils.BOOT_TIME
import xyz.wingio.logra.utils.Channels
import xyz.wingio.logra.utils.Intents

class CrashReceiver : BroadcastReceiver(), KoinComponent {

    private var packageName: String? = null
    private var description: String? = null
    private var stackTrace: String? = null
    private val db: CrashesDatabase by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intents.Actions.EXCEPTION)
            return

        description = intent.getStringExtra(Intents.Extras.DESCRIPTION)!!
        if (description!!.startsWith(ThreadDeath::class.java.name))
            return

        packageName = intent.getStringExtra(Intents.Extras.PACKAGE_NAME)
        val time = intent.getLongExtra(Intents.Extras.TIME, System.currentTimeMillis())
        stackTrace = intent.getStringExtra(Intents.Extras.STACKTRACE)

        println("App has crashed")

        val pm = context.packageManager
        val appInfo = packageName?.let {
            pm.getPackageInfo(
                it,
                PackageManager.PackageInfoFlags.of(0)
            )
        }?.applicationInfo
        val crash = Crash(
            packageName = packageName ?: "unknown",
            time = time,
            stacktrace = stackTrace ?: "",
            id = 0
        )
        crash.id = addToDB(crash)

        val notificationId = (time - BOOT_TIME).toInt()

        val clickIntent = PendingIntent.getActivity(
            context,
            notificationId,
            Intent(context, MainActivity::class.java).apply {
                putExtra(Intents.Extras.CRASH, crash)
                action = Intents.Actions.VIEW_CRASH
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        NotificationCompat.Builder(context, Channels.CRASH_DETECTOR).apply {
            setSmallIcon(R.drawable.ic_bug_24)
            setLargeIcon(appInfo?.loadIcon(pm)?.toBitmap())
            setContentTitle("${appInfo?.loadLabel(pm)} crashed")
            setContentText(description)
            priority = NotificationCompat.PRIORITY_MAX
            setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            setAutoCancel(true)
            setOnlyAlertOnce(true)
            foregroundServiceBehavior = NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
            setContentIntent(clickIntent)
            setNotificationStyle()
            addButtons(context, notificationId)

            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, build())
        }

    }

    private fun NotificationCompat.Builder.setNotificationStyle(): NotificationCompat.Builder {
        val style = NotificationCompat.InboxStyle()
        val traces = stackTrace!!.split("\n".toRegex()).toTypedArray()
        var i = 2
        while (i < 8 && i < traces.size) {
            style.addLine(traces[i])
            i++
        }
        return setStyle(style)
    }

    private fun NotificationCompat.Builder.addButtons(
        context: Context,
        notId: Int
    ): NotificationCompat.Builder = with(this) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, stackTrace)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(shareIntent, null)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        addAction(
            NotificationCompat.Action(
                0,
                context.getString(R.string.share),
                PendingIntent.getActivity(
                    context,
                    notId,
                    chooser,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        )
    }

    private fun addToDB(crash: Crash): Long {
        var id = -1L
        val job = CoroutineScope(Dispatchers.IO).launch {
            id = db.crashesDao().insert(crash)
        }
        while (job.isActive) {
        }
        return id
    }
}