package xyz.wingio.logra

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import xyz.wingio.logra.ui.components.PermissionPopup
import xyz.wingio.logra.ui.screens.main.MainScreen
import xyz.wingio.logra.ui.theme.LograTheme
import xyz.wingio.logra.utils.logcat.LogcatManager

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LogcatManager.connect()

        setContent {
            val isDark = isSystemInDarkTheme()
            LograTheme(isDark) {
                val systemUiController = rememberSystemUiController()
                val surface = MaterialTheme.colorScheme.surface
                SideEffect {
                    systemUiController.apply {
                        setSystemBarsColor(
                            color = surface,
                            darkIcons = !isDark,
                        )
                        isNavigationBarContrastEnforced = true
                    }
                }

                Navigator(screen = MainScreen()) {
                    SlideTransition(it)
                }

                if (ContextCompat.checkSelfPermission(LocalContext.current, Manifest.permission.READ_LOGS) == PackageManager.PERMISSION_DENIED) {
                    PermissionPopup()
                }
            }
        }
    }
}