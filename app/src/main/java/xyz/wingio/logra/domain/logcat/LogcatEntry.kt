package xyz.wingio.logra.domain.logcat

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.utils.Utils.cleanLine
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

data class LogcatEntry(
    val createdAt: Date,
    val pid: Int,
    val level: LogLevel,
    val tag: String,
    val content: String,
    val raw: String
) {

    val annotated: AnnotatedString
        @Composable get() = buildAnnotatedString {
            val prefs: PreferenceManager = get()
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    background = level.color.copy(0.4f)
                )
            ) {
                append(" ${level.name[0]} ")
            }
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.ExtraBold,
                    background = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                )
            ) {
                append(SimpleDateFormat(" ${prefs.timestampFormat} ").format(createdAt))
            }
            append(" $pid $tag: $content")
        }

    companion object {

        private val logcatRegex = Pattern.compile(
            "^ *(\\d+)\\.(\\d{1,3}) *([\\d]+) *[\\d]+ *([VDIWEFS]) *([\\d\\D]*?) *: *([\\d\\D]*)\$",
            Pattern.MULTILINE
        )

        fun fromLine(line: String): LogcatEntry? = with(line) {

            val parsed = logcatRegex.matcher(line.cleanLine())

            if (!parsed.find()) return@with null

            val secs = parsed.group(1)
            val millis = parsed.group(2)
            val level = parsed.group(4)
            val tag = parsed.group(5)
            val pid = parsed.group(3)
            val content = parsed.group(6)

            LogcatEntry(
                createdAt = Date("$secs$millis".toLong()),
                pid = pid?.toInt() ?: 0,
                level = LogLevel.values().first { it.name.startsWith(level ?: "S") },
                tag = tag?.trim() ?: "",
                content = content?.trim() ?: "",
                raw = line.cleanLine()
            )
        }
    }

}