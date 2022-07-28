package xyz.wingio.logra.domain.logcat.filter

import xyz.wingio.logra.domain.logcat.LogLevel
import java.util.*

data class Filter(
    var text: String = "",
    var tag: String = "",
    var regex: Boolean = false,
    var levels: MutableList<LogLevel> = LogLevel.values().toMutableList(),
    var after: Date = Date(0),
    var before: Date = Date(Long.MAX_VALUE)
)