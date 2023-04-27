package xyz.wingio.logra.domain.manager

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.wingio.logra.app.App
import xyz.wingio.logra.domain.logcat.LogcatEntry
import xyz.wingio.logra.utils.Logger
import xyz.wingio.logra.utils.Utils.updateList
import xyz.wingio.logra.utils.mainThread
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.concurrent.thread

class LogcatManager : KoinComponent {

    private val command = "logcat -v epoch -T 0"
    private val prefs: PreferenceManager by inject()

    private lateinit var reader: BufferedReader
    private lateinit var proc: Process

    val logs = MutableStateFlow(emptyList<LogcatEntry>())
    private var job: Job? = null

    fun stop() {
        job?.cancel()
        reader.close()
        proc.destroy()
    }

    fun clear() {
        logs.update { emptyList() }
    }

    fun connect() {
        if (prefs.dumpLogs) Runtime.getRuntime().exec("logcat -c").waitFor()
        proc = Runtime.getRuntime().exec(command)
        reader = proc.inputStream.bufferedReader()
    }

    fun listen() {
        if(!proc.isAlive) connect()
        job = App.applicationScope.launch(Dispatchers.IO) {
            while (proc.isAlive) {
                reader.forEachLine {
                    val ent = LogcatEntry.fromLine(it)
                    ent?.let {
                        logs.updateList {
                            add(it)

                            while (size > 10_000) {
                                removeAt(0)
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun getLogsFromPid(pid: Int): List<LogcatEntry> = withContext(Dispatchers.IO) {
        val logger = Logger("Exporter")
        logger.debug("Getting logs for $pid...")
        if(Build.VERSION.SDK_INT >= 24) {
            val logs = mutableListOf<LogcatEntry>()

            logger.debug("Running logcat command...")
            val process = Runtime
                .getRuntime()
                .exec("logcat -v epoch -d --pid=${pid}")
                .apply { waitFor() }

            if (process.exitValue() == 0) {
                logger.debug("Reading command output...")
                process.inputStream.bufferedReader().forEachLine {
                    LogcatEntry.fromLine(it)?.let { log ->
                        logs.add(log)
                    }
                }
            } else {
                logger.error("Failed to get logs")
            }

            logs
        } else {
            //TODO: Fallback method for devices running Android 6 and lower
            emptyList()
        }
    }

}