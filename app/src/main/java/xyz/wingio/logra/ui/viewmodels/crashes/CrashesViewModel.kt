package xyz.wingio.logra.ui.viewmodels.crashes

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.wingio.logra.crashdetector.db.CrashesDatabase
import xyz.wingio.logra.crashdetector.db.entities.Crash

class CrashesViewModel(
    db: CrashesDatabase
) : ScreenModel {

    var crashes = mutableStateListOf<Crash>()

    private val dao = db.crashesDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        getCrashes()
    }

    fun deleteCrash(id: Long) {
        scope.launch {
            dao.deleteCrash(id)
        }
        crashes.removeIf { it.id == id }
    }

    fun clearCrashes() {
        scope.launch {
            dao.clear()
        }
        crashes.clear()
    }

    fun getCrashes() {
        scope.launch {
            dao.getAll().also {
                crashes = it.toMutableStateList()
            }
        }
    }

}