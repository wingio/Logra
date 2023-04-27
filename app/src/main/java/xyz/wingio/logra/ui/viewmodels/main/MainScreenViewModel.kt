package xyz.wingio.logra.ui.viewmodels.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.wingio.logra.domain.logcat.LogcatEntry
import xyz.wingio.logra.domain.logcat.filter.Filter
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.utils.Utils.matches
import xyz.wingio.logra.domain.manager.LogcatManager

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModel(
    val prefs: PreferenceManager,
    val logcatManager: LogcatManager
) : ScreenModel {

    var paused by mutableStateOf(false)
    var freeScroll by mutableStateOf(false)
    var filterOpened by mutableStateOf(false)
    var searchByOpen by mutableStateOf(false)
    val selectedLogs = mutableStateListOf<LogcatEntry>()
    var filter by mutableStateOf(Filter())
    val logs = MutableStateFlow(emptyList<LogcatEntry>())

    init {
        logcatManager.connect()
        logcatManager.listen()
    }

//    fun filterLogs(): MutableStateFlow<List<LogcatEntry>> {
//        return LogcatManager.logs.matches(filter)
//    }

    fun stop() = logcatManager.stop()

    fun start() = logcatManager.listen()

    fun clear() = logcatManager.clear()

    fun searchByText() {
        filter.text = selectedLogs.firstOrNull()?.content ?: ""
        searchByOpen = false
    }

    fun searchByTag() {
        filter.tags.addAll(selectedLogs.map { it.tag })
        searchByOpen = false
    }

    fun searchByPid() {
        filter.pids.addAll(selectedLogs.map { it.pid })
        searchByOpen = false
    }
}