package xyz.wingio.logra.utils

import android.util.Log

class Logger(private val tag: String) {

    fun verbose(message: String) = log(LoggedItem.Type.VERBOSE, message)
    fun debug(message: String) = log(LoggedItem.Type.DEBUG, message)
    fun info(message: String) = log(LoggedItem.Type.INFO, message)
    fun warn(message: String) = log(LoggedItem.Type.WARN, message)
    fun error(message: String, th: Throwable? = null) = log(LoggedItem.Type.ERROR, message, th)

    private fun log(type: LoggedItem.Type, message: String, th: Throwable? = null) {
        when (type) {
            LoggedItem.Type.VERBOSE -> Log.v(APP_NAME, "[$tag] $message", th)
            LoggedItem.Type.DEBUG -> Log.d(APP_NAME, "[$tag] $message", th)
            LoggedItem.Type.INFO -> Log.i(APP_NAME, "[$tag] $message", th)
            LoggedItem.Type.WARN -> Log.w(APP_NAME, "[$tag] $message", th)
            LoggedItem.Type.ERROR -> Log.e(APP_NAME, "[$tag] $message", th)
        }
        logs.add(
            LoggedItem(
                type = type,
                message = "[$tag] $message",
                error = th
            )
        )
    }

    companion object {
        const val APP_NAME = "Logra"
        val logs: MutableList<LoggedItem> by lazy { mutableListOf() }
        val default = Logger(APP_NAME)
        fun log(message: String) = default.info(message)
    }

    data class LoggedItem(val type: Type, val message: String, val error: Throwable? = null) {
        enum class Type {
            INFO,
            WARN,
            DEBUG,
            ERROR,
            VERBOSE
        }
    }

}