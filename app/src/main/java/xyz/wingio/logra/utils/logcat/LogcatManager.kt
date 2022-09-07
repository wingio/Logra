package xyz.wingio.logra.utils.logcat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.wingio.logra.domain.logcat.LogcatEntry
import java.io.BufferedReader
import kotlin.concurrent.thread

object LogcatManager {

    private const val command = "logcat -v epoch"

    private lateinit var reader: BufferedReader
    private lateinit var proc: Process

    private lateinit var logFlow: Flow<LogcatEntry>

    fun connect() {
        proc = Runtime.getRuntime().exec(command)
        reader = proc.inputStream.bufferedReader()
    }

    suspend fun listen(callback: (LogcatEntry) -> Unit) {
        logFlow.collect {
            callback(it)
        }
    }

    fun start() {
        logFlow = flow {
            while (true) {
                if (!proc.isAlive) {
                    connect(); continue
                }
                if (reader.readLine() != null && reader.readLine().isNotEmpty()) {
                    val line = reader.readLine()
                    line.split("\n").forEach {
                        try {
                            val ent = LogcatEntry.fromLine(it)
                            ent?.let { log ->
                                emit(log)
                            }
                        } catch (th: Throwable) {
                        }
                    }
                }
            }
        }
    }

}