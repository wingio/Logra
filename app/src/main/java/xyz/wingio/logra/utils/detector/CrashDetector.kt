package xyz.wingio.logra.utils.detector

import android.content.Context
import android.content.Intent
import xyz.wingio.logra.BuildConfig
import xyz.wingio.logra.crashdetector.receivers.CrashReceiver
import xyz.wingio.logra.domain.logcat.LogcatEntry
import xyz.wingio.logra.utils.Intents
import xyz.wingio.logra.utils.Logger
import java.io.BufferedReader
import kotlin.concurrent.thread

class CrashDetector(private val context: Context) {

    private val proc: Process
    private val reader: BufferedReader
    private val logger = Logger("Crash Detector")

    init {
        Runtime.getRuntime().exec("logcat -c").waitFor()
        proc = Runtime.getRuntime().exec("logcat -v epoch *:E")
        reader = proc.inputStream.bufferedReader()

        thread(start = true) {
            logger.debug("Started listening for crashes...")
            reader.forEachLine {
                val log = LogcatEntry.fromLine(it)
                if (log != null && log.content.matches(fePattern) && log.tag == "AndroidRuntime") {
                    reportCrash(reader, log.content)
                }
            }
        }
    }

    private fun reportCrash(reader: BufferedReader, firstLine: String): LogcatEntry? {

        val logs = mutableListOf<LogcatEntry>()
        var lastLog: LogcatEntry?
        var index = 0
        var foundTrace = false
        var foundTraceAt = 0

        while (true) {
            val log = LogcatEntry.fromLine(reader.readLine(), false)
            lastLog = log
            if (log != null && log.tag == "AndroidRuntime") {
                if (!foundTrace && log.content.matches(linePattern)) {
                    foundTrace = true
                    foundTraceAt = index
                }
                logs.add(log)
            } else {
                break
            }
            index++
        }
        if (logs.size < 2) return lastLog

        val packageName =
            processInfoPattern.find(logs[0].content)?.groupValues?.get(1) ?: return lastLog

        val intent = Intent(Intents.Actions.EXCEPTION)
            .setClassName(BuildConfig.APPLICATION_ID, CrashReceiver::class.java.name)
            .putExtra(Intents.Extras.PACKAGE_NAME, packageName)
            .putExtra(Intents.Extras.TIME, System.currentTimeMillis())
            .putExtra(Intents.Extras.DESCRIPTION,
                logs.subList(1, foundTraceAt).joinToString("\n") { it.content })
            .putExtra(
                Intents.Extras.STACKTRACE,
                "$firstLine\n" + logs.joinToString("\n") { it.content })

        context.sendBroadcast(intent)

        return lastLog
    }

    fun kill() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    companion object {
        private val fePattern =
            Regex("FATAL EXCEPTION: (?:.)*")
        private val linePattern =
            Regex("\tat(.)*")
        private val processInfoPattern =
            Regex("Process: ([a-z][a-z0-9_]*(?:\\.[a-z0-9_]+)+[0-9a-z_]), PID: ([0-9]+)")
    }

}