package xyz.wingio.logra.domain.logcat.filter

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import xyz.wingio.logra.domain.logcat.LogLevel
import java.time.LocalDate

@Stable
class Filter {
    var text by mutableStateOf("")
    var tags = mutableStateListOf<String>()
    var pids = mutableStateListOf<Int>()
    var regex by mutableStateOf(false)
    var levels = LogLevel.values().toMutableList().toMutableStateList()
    var after by mutableStateOf(LocalDate.MIN)
    var before by mutableStateOf(LocalDate.MAX)

    fun reset() {
        regex = DEFAULT.regex
        levels.clear()
        levels.addAll(DEFAULT.levels)
        after = DEFAULT.after
        before = DEFAULT.before
    }

    companion object {
        val DEFAULT = Filter()
    }

}