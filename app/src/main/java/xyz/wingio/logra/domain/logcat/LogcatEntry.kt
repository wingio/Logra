package xyz.wingio.logra.domain.logcat

import xyz.wingio.logra.utils.Utils.cleanLine
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

data class LogcatEntry (
    val createdAt: Date,
    val pid: Int,
    val level: LogLevel,
    val tag: String,
    val content: String,
    val raw: String
) {

    companion object  {

        private val logcatRegex = Pattern.compile("^(\\d{0,2}-\\d{0,2}) *(\\d{0,2}:\\d{0,2}:\\d{0,2}\\.\\d+?) *([\\d]+) *[\\d]+ *([VDIWEFS]) *([\\d\\D]*?) *: *([\\d\\D]*)\$", Pattern.MULTILINE)

        fun fromLine(line: String): LogcatEntry? = with(line) {

            val parsed = logcatRegex.matcher(line.cleanLine())

            if(!parsed.find()) return@with null

            val date = parsed.group(1)
            val time = parsed.group(2)
            val level = parsed.group(4)
            val tag = parsed.group(5)
            val pid = parsed.group(3)
            val content = parsed.group(6)

            LogcatEntry(
                createdAt = SimpleDateFormat("MM-dd hh:mm:ss.S", Locale.US).parse("$date $time")!!,
                pid = pid?.toInt() ?: 0,
                level = LogLevel.values().first { it.name.startsWith(level ?: "S") },
                tag = tag?.trim() ?: "",
                content = content?.trim() ?: "",
                raw = line.cleanLine()
            )
        }
    }

}