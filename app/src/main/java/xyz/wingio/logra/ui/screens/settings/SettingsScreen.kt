package xyz.wingio.logra.ui.screens.settings

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.ui.components.SettingsSwitch

class SettingsScreen : Screen, KoinComponent {

    val prefs: PreferenceManager by inject()

    @Composable
    override fun Content() = Screen()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Screen() {
        Scaffold(
            Modifier.fillMaxSize(),
            topBar = { Toolbar() }
        ) { paddingValues ->
            Column(
                Modifier.padding(paddingValues)
            ) {
                SettingsSwitch(
                    label = "Dynamic Theming",
                    pref = prefs.monet,
                    disabled = Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                ) { prefs.monet = it }
                SettingsSwitch(label = "Line Wrap", pref = prefs.lineWrap) { prefs.lineWrap = it }
                SettingsSwitch(label = "Compact Mode", pref = prefs.compact) { prefs.compact = it }
            }
        }
    }

    @Composable
    private fun Toolbar() {
        LargeTopAppBar(
            title = { Text("Settings") }
        )
    }

}