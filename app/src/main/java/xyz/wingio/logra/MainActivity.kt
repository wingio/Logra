package xyz.wingio.logra

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import xyz.wingio.logra.crashdetector.db.entities.Crash
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.domain.manager.Theme
import xyz.wingio.logra.ui.components.permission.PermissionPopup
import xyz.wingio.logra.ui.screens.crashes.CrashDetailScreen
import xyz.wingio.logra.ui.screens.main.MainScreen
import xyz.wingio.logra.ui.theme.LograTheme
import xyz.wingio.logra.utils.Intents
import xyz.wingio.logra.utils.Utils
import xyz.wingio.logra.utils.Utils.saveText
import xyz.wingio.logra.utils.hasLogsPermission
import xyz.wingio.logra.utils.initCrashService
import xyz.wingio.logra.utils.logcat.LogcatManager
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    val prefs: PreferenceManager by inject()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        LogcatManager.connect()
        val showCrash =
            intent.action == Intents.Actions.VIEW_CRASH && intent.hasExtra(Intents.Extras.CRASH)
        if (prefs.crashDetectorEnabled) initCrashService()

        setContent {

            val isDark = when (prefs.theme) {
                Theme.SYSTEM -> isSystemInDarkTheme()
                Theme.LIGHT -> false
                Theme.DARK -> true
            }

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

                Navigator(
                    screen = if (showCrash) {
                        val crash =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                intent.getSerializableExtra(
                                    Intents.Extras.CRASH,
                                    Crash::class.java
                                )!!
                            } else {
                                @Suppress("DEPRECATION")
                                intent.getSerializableExtra(Intents.Extras.CRASH) as Crash
                            }
                        CrashDetailScreen(crash)
                    } else MainScreen()
                ) {
                    SlideTransition(it)
                }

                if (!hasLogsPermission) {
                    PermissionPopup()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) {
                try {
                    val fos = contentResolver.openOutputStream(data.data!!)
                    fos?.write(Utils.textToSave.toByteArray())
                    fos?.flush()
                    fos?.close()

                    Utils.showToast(getString(R.string.save_successful))
                } catch (e: Throwable) {
                    Utils.showToast(getString(R.string.save_failed))
                }

                Utils.textToSave = ""
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if(it.action == "logra.actions.EXPORT_LOGS") {
                val pid = it.extras?.getInt("logra.extras.PID") ?: return
                CoroutineScope(Dispatchers.IO).launch {
                    LogcatManager.getLogsFromPid(pid).also { logs ->
                        saveText(
                            logs
                                .sortedBy { log -> log.createdAt }
                                .joinToString("\n") { log ->
                                    log.raw
                                },
                            "Logcat (${pid}) ${SimpleDateFormat("M/dd/yy H:mm:ss.SSS").format(Date())}"
                        )
                    }
                }
            }
        }
    }
}