package xyz.wingio.logra.ui.viewmodels.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val scope = CoroutineScope(Dispatchers.IO)
    private val pausedLogs = mutableListOf<LogcatEntry>()

    init {
        scope.launch(Dispatchers.IO) {
            LogcatManager.listen {
                if (paused)
                    pausedLogs.add(it)
                else {
                    logs.addAll(pausedLogs)
                    pausedLogs.clear()
                    logs.add(it)
                }
            }
        }
    }

    fun filterLogs(): List<LogcatEntry> {
        val newList = mutableListOf<LogcatEntry>()
        for (i in 0 until logs.lastIndex) {
            val item = logs.getSafelyOrNull(i) ?: continue
            if (!item.matches(filter)) continue
            newList.add(item)
        }
        return newList
    }
}