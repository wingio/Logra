package xyz.wingio.logra.domain.logcat

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.domain.manager.Theme

enum class LogLevel : KoinComponent {
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    FATAL;

    val colorMap: Map<LogLevel, Pair<Color, Color>>
        get() {
            val prefs: PreferenceManager by inject()
            return mapOf(
                VERBOSE to (prefs.colorVL to prefs.colorVD),
                DEBUG to (prefs.colorDL to prefs.colorDD),
                INFO to (prefs.colorIL to prefs.colorID),
                WARNING to (prefs.colorWL to prefs.colorWD),
                ERROR to (prefs.colorEL to prefs.colorED),
                FATAL to (prefs.colorFL to prefs.colorFD),
            )
        }

    val color: Color
        @Composable get() {
            val prefs: PreferenceManager by inject()
            val (light, dark) = colorMap[this]!!
            val isDark = when (prefs.theme) {
                Theme.SYSTEM -> isSystemInDarkTheme()
                Theme.LIGHT -> false
                Theme.DARK -> true
            }

            return if (isDark)
                dark
            else
                light
        }
}