package xyz.wingio.logra

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.core.content.ContextCompat
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuRemoteProcess
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.ui.screens.main.MainScreen
import xyz.wingio.logra.ui.theme.LograTheme
import xyz.wingio.logra.utils.Logger
import xyz.wingio.logra.utils.Utils
import xyz.wingio.logra.utils.checkShizukuPermission
import xyz.wingio.logra.utils.grantPermissionWithShizuku
import xyz.wingio.logra.utils.logcat.LogcatManager

class MainActivity : ComponentActivity() {

    private val prefs: PreferenceManager by inject()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
            val hasRoot = Utils.checkRoot()
            prefs.hasRoot = hasRoot
            Logger("RootChecker").debug(hasRoot.toString())

            // If root fails, attempt to use Shizuku
            val shizukuLogger = Logger("Shizuku")
            val shizukuGranted = checkShizukuPermission()
            shizukuLogger.debug("Shizuku perms granted: $shizukuGranted")
            if (shizukuGranted) {
                shizukuLogger.debug("Attempting to use shizuku to grant permissions")
                val granted = grantPermissionWithShizuku()
                shizukuLogger.debug("Perm now granted: $granted")
            }
        }

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

            }
        }
    }
}