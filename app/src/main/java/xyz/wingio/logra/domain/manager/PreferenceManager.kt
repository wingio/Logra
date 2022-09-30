package xyz.wingio.logra.domain.manager

import android.content.Context
import xyz.wingio.logra.domain.manager.base.BasePreferenceManager

class PreferenceManager(context: Context) :
    BasePreferenceManager(context.getSharedPreferences("prefs", Context.MODE_PRIVATE)) {

    var compact by booleanPreference("compact_mode", false)

    var lineWrap by booleanPreference("line_wrap", true)

    var monet by booleanPreference("monet", true)

    var timestampFormat by stringPreference("timestamp_format", "h:mm:ssa")

    var theme by enumPreference("theme", Theme.SYSTEM)

    var crashDetectorEnabled by booleanPreference("crash_detector_enabled", false)
}

enum class Theme {
    SYSTEM,
    LIGHT,
    DARK
}