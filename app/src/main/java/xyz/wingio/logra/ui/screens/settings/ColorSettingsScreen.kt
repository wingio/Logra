package xyz.wingio.logra.ui.screens.settings

//import xyz.wingio.logra.ui.components.tabs.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.androidx.compose.get
import xyz.wingio.logra.R
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.ui.widgets.settings.colors.ColorOption

class ColorSettingsScreen : Screen {

    @Composable
    override fun Content() = Screen()

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun Screen(
        prefs: PreferenceManager = get()
    ) {
        Scaffold(
            topBar = {
                TopBar()
            }
        ) {
            Column(
                Modifier
                    .padding(it)
            ) {
                ColorOption(
                    lightColor = prefs.colorVL,
                    darkColor = prefs.colorVD,
                    label = { Text(stringResource(id = R.string.l_verbose)) }
                ) { l, d ->
                    prefs.colorVL = l
                    prefs.colorVD = d
                }
                ColorOption(
                    lightColor = prefs.colorIL,
                    darkColor = prefs.colorID,
                    label = { Text(stringResource(id = R.string.l_info)) }
                ) { l, d ->
                    prefs.colorIL = l
                    prefs.colorID = d
                }
                ColorOption(
                    lightColor = prefs.colorDL,
                    darkColor = prefs.colorDD,
                    label = { Text(stringResource(id = R.string.l_debug)) }
                ) { l, d ->
                    prefs.colorDL = l
                    prefs.colorDD = d
                }
                ColorOption(
                    lightColor = prefs.colorWL,
                    darkColor = prefs.colorWD,
                    label = { Text(stringResource(id = R.string.l_warn)) }
                ) { l, d ->
                    prefs.colorWL = l
                    prefs.colorWD = d
                }
                ColorOption(
                    lightColor = prefs.colorEL,
                    darkColor = prefs.colorED,
                    label = { Text(stringResource(id = R.string.l_error)) }
                ) { l, d ->
                    prefs.colorEL = l
                    prefs.colorED = d
                }
                ColorOption(
                    lightColor = prefs.colorFL,
                    darkColor = prefs.colorFD,
                    label = { Text(stringResource(id = R.string.l_fatal)) }
                ) { l, d ->
                    prefs.colorFL = l
                    prefs.colorFD = d
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun TopBar() {
        val navigator = LocalNavigator.current

        LargeTopAppBar(
            title = {
                Text(stringResource(R.string.settings_colors))
            },
            navigationIcon = {
                IconButton(onClick = { navigator?.pop() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(
                            id = R.string.back
                        )
                    )
                }
            }
        )
    }

}