package xyz.wingio.logra.ui.viewmodels.crashes

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.wingio.logra.crashdetector.db.CrashesDatabase

class CrashDetailViewModel(
    db: CrashesDatabase
) : ScreenModel {

    private val dao = db.crashesDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun deleteCrash(id: Long) {
        scope.launch {
            dao.deleteCrash(id)
        }
    }

}