package xyz.wingio.logra.ui.viewmodels.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import xyz.wingio.logra.domain.logcat.LogcatEntry
import xyz.wingio.logra.domain.logcat.filter.Filter
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.utils.Utils.matches
import xyz.wingio.logra.utils.logcat.LogcatManager

class MainScreenViewModel(
    val prefs: PreferenceManager
) : ViewModel() {

    var logs = mutableStateListOf<LogcatEntry>()
    private val unfilteredLogs = mutableListOf<LogcatEntry>()

    private val pausedLogs = mutableListOf<LogcatEntry>()

    val paused = mutableStateOf(false)
    val freeScroll = mutableStateOf(false)
    val filterOpened = mutableStateOf(false)

    var filter = mutableStateOf(Filter())

    init {
        LogcatManager.listen {
            if (paused.value)
                pausedLogs.add(it)
            else {
                logs.addAll(pausedLogs)
                pausedLogs.clear()
                logs.add(it)
            }
        }
    }

    fun filterLogs(): List<LogcatEntry> {
        return logs.matches(filter.value)
    }

}