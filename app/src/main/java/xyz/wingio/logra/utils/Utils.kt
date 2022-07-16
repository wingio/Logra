package xyz.wingio.logra.utils

import android.Manifest
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.wingio.logra.BuildConfig
import xyz.wingio.logra.domain.manager.PreferenceManager
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*
import java.util.concurrent.Executors


object Utils : KoinComponent {

    private val prefs: PreferenceManager by inject()

    fun String.cleanLine() = replace(" {2,}".toRegex(), "")

    private fun BufferedWriter.writeCmd(cmd: String) {
        write(cmd)
        newLine()
        flush()
    }

    suspend fun checkRoot() = coroutineScope {
        if (prefs.hasRoot) return@coroutineScope true
        try {
            val pBuilder = ProcessBuilder("su")
            val proc = withContext(Dispatchers.IO) { pBuilder.start() }

            val stdout = BufferedWriter(OutputStreamWriter(proc.outputStream))
            val stdin = BufferedReader(InputStreamReader(proc.inputStream))

            val marker = "RESULT>>>${UUID.randomUUID()}>>>"

            val stdoutStderrDispatcherContext =
                Executors.newFixedThreadPool(2).asCoroutineDispatcher()
            val stdoutResult = async(stdoutStderrDispatcherContext) {
                var res = false
                try {
                    while (true) {
                        val line = stdin.readLine()?.trim() ?: break
                        val index = line.indexOf(marker)

                        if (index != -1) {
                            res = line.substring(index + marker.length) == "0"
                            break
                        }
                    }
                } catch (th: Throwable) {
                }

                res
            }

            val result = stdoutResult.await()

            stdout.writeCmd("pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.READ_LOGS}")
            stdout.writeCmd("echo \"$marker$?\"")

            stdout.writeCmd("exit")
            withContext(Dispatchers.IO) { proc.waitFor() }
            proc.destroy()

            result
        } catch (th: Throwable) {
            false
        }
    }

    context(Context)
    fun showToast(text: String) {
        Toast.makeText(this@Context, text, Toast.LENGTH_LONG).show()
    }

}