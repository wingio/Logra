package xyz.wingio.logra.domain.logcat.filter

import xyz.wingio.logra.domain.logcat.LogLevel
import java.time.LocalDate
import java.util.*

data class Filter(
    var text: String = "",
    var tags: MutableList<String> = mutableListOf(),
    var regex: Boolean = false,
    var levels: MutableList<LogLevel> = LogLevel.values().toMutableList(),
    var after: LocalDate = LocalDate.MIN,
    var before: LocalDate = LocalDate.MAX
)