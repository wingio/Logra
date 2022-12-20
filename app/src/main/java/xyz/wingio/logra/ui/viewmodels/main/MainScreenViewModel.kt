package xyz.wingio.logra.ui.viewmodels.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import xyz.wingio.logra.domain.logcat.LogcatEntry
import xyz.wingio.logra.domain.logcat.filter.Filter
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.utils.Utils.getSafelyOrNull
import xyz.wingio.logra.utils.Utils.matches
import xyz.wingio.logra.utils.logcat.LogcatManager

class MainScreenViewModel(
    val prefs: PreferenceManager
) : ScreenModel {

    var paused by mutableStateOf(false)
    var freeScroll by mutableStateOf(false)
    var filterOpened by mutableStateOf(false)
    val selectedLogs = mutableStateListOf<LogcatEntry>()
    var filter by mutableStateOf(Filter())
    var logs = mutableStateListOf<LogcatEntry>()

    private val pausedLogs = mutableListOf<LogcatEntry>()

    init {
        LogcatManager.listen {
            if (paused)
                pausedLogs.add(it)
            else {
                addLogs(pausedLogs)
                pausedLogs.clear()
                addLog(it)
            }
        }
    }

    private fun addLog(entry: LogcatEntry) {
        if (logs.size > 10000) {
            logs.removeFirst()
        }
        logs.add(entry)
    }

    private fun addLogs(entries: List<LogcatEntry>) {
        entries.forEach {
            addLog(it)
        }
    }

    fun filterLogs(): List<LogcatEntry> {
        return logs.matches(filter)
    }
}