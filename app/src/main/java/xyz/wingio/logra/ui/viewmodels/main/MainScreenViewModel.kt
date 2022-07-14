package xyz.wingio.logra.ui.viewmodels.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import xyz.wingio.logra.domain.logcat.LogcatEntry
import xyz.wingio.logra.utils.logcat.LogcatManager
import kotlin.concurrent.thread

class MainScreenViewModel: ViewModel() {

    val logs = mutableStateListOf<LogcatEntry>()
    private val pausedLogs = mutableListOf<LogcatEntry>()
    val paused = mutableStateOf(false)
    val freeScroll = mutableStateOf(false)

    init {
        LogcatManager.listen {
            if(paused.value)
                pausedLogs.add(it)
            else {
                logs.addAll(pausedLogs)
                pausedLogs.clear()
                logs.add(it)
            }
        }
    }

}