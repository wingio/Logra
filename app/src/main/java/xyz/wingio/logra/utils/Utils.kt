package xyz.wingio.logra.utils

import android.content.Context
import android.widget.Toast
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

}