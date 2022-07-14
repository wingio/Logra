package xyz.wingio.logra.domain.manager

import android.content.Context
import xyz.wingio.logra.domain.manager.base.BasePreferenceManager

class PreferenceManager(context: Context): BasePreferenceManager(context.getSharedPreferences("prefs", Context.MODE_PRIVATE)) {

    var hasRoot by booleanPreference("has_elev_access", false)

}