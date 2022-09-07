package xyz.wingio.logra.utils

import android.content.Context
import android.widget.Toast
import xyz.wingio.logra.domain.logcat.LogcatEntry
import xyz.wingio.logra.domain.logcat.filter.Filter
import java.io.BufferedWriter

object Utils {

    fun String.cleanLine() = replace(" {2,}".toRegex(), "")

    private fun BufferedWriter.writeCmd(cmd: String) {
        write(cmd)
        newLine()
        flush()
    }

    context(Context)
    fun showToast(text: String) {
        Toast.makeText(this@Context, text, Toast.LENGTH_LONG).show()
    }

    val Enum<*>.capitalizedName: String
        get() = name[0] + name.lowercase().slice(1 until name.length)

    fun List<LogcatEntry>.matches(filter: Filter) = filter {
        it.matches(filter)
    }

    fun LogcatEntry.matches(filter: Filter): Boolean {
        val textMatches = filter.text.isBlank() || content.contains(filter.text)
        val levelMatches = filter.levels.contains(level)
        val tagMatches = filter.tags.isEmpty() || filter.tags.map { it.lowercase() }.contains(tag.lowercase())

        return textMatches && levelMatches && tagMatches
    }

}