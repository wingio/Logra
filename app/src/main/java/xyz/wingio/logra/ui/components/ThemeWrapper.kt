package xyz.wingio.logra.ui.components

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.get
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.domain.manager.Theme

@Composable
fun ThemeWrapper(
    theme: Theme,
    prefs: PreferenceManager = get(),
    content: @Composable () -> Unit
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && prefs.monet
    val darkTheme = if (theme == Theme.SYSTEM) isSystemInDarkTheme() else theme == Theme.DARK
    val colors = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colors
    ) {
        content()
    }
}