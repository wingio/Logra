package xyz.wingio.logra.utils.logcat

import xyz.wingio.logra.domain.logcat.LogcatEntry
import java.io.BufferedReader
import java.io.InputStream
import kotlin.concurrent.thread

object LogcatManager {

    private const val command = "logcat"

    private lateinit var reader: BufferedReader
    private lateinit var proc: Process

    fun connect() {
        proc = Runtime.getRuntime().exec(command)
        reader = proc.inputStream.bufferedReader()
    }

    fun listen(callback: (LogcatEntry) -> Unit) {
        thread(start = true) {
            while (true) {
                if(!proc.isAlive) { connect(); continue }
                if(reader.readLine() != null && reader.readLine().isNotEmpty()) {
                    val line = reader.readLine()
                    line.split("\n").forEach {
                        try {
                            val ent = LogcatEntry.fromLine(it)
                            ent?.let(callback)
                        } catch (th: Throwable) {
                        }
                    }
                }
            }
        }
    }

}