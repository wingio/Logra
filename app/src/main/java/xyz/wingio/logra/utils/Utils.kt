package xyz.wingio.logra.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import xyz.wingio.logra.domain.logcat.LogcatEntry
import xyz.wingio.logra.domain.logcat.filter.Filter
import java.io.BufferedWriter

object Utils {
    var textToSave = ""

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
        val tagMatches =
            filter.tags.isEmpty() || filter.tags.map { it.lowercase() }.contains(tag.lowercase())

        return textMatches && levelMatches && tagMatches
    }

    fun <I> MutableList<I>.addUnique(item: I) {
        if (!contains(item)) add(item)
    }

    fun Context.shareText(text: CharSequence) = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
        Intent.createChooser(this, null).also {
            this@shareText.startActivity(it)
        }
    }

    fun Context.saveText(text: String, title: CharSequence) =
        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"

            putExtra(Intent.EXTRA_TITLE, "$title.txt")

            textToSave = text

            (this@saveText as Activity).startActivityForResult(this, 1)
        }

}