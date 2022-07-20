package xyz.wingio.logra.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.get
import xyz.wingio.logra.domain.manager.PreferenceManager

val ColorScheme.logLineAlt: Color
    get() = onSurface.copy( alpha = 0.7f )

@Composable
fun LograTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val prefs: PreferenceManager = get()
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && prefs.monet
    val colors = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}