package xyz.wingio.logra.ui.screens.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Feed
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.androidx.compose.get
import xyz.wingio.logra.BuildConfig
import xyz.wingio.logra.R
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.ui.components.settings.SettingItem
import xyz.wingio.logra.utils.Utils

class AboutScreen : Screen {

    @Composable
    override fun Content() = Screen()

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun Screen(
        prefs: PreferenceManager = get()
    ) {
        val ctx = LocalContext.current
        val uriHandler = LocalUriHandler.current
        var tappedCount = 0

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { Toolbar() }
        ) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                SettingItem(
                    icon = {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.version)
                        )
                    },
                    text = { Text(stringResource(R.string.version)) },
                    secondaryText = { Text("${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})") },
                    modifier = Modifier.clickable {
                        tappedCount += 1
                        with(ctx) {
                            if (prefs.easterEggDiscovered)
                                Utils.showToast(getString(R.string.easter_egg_found))
                            else if (tappedCount >= 10) {
                                Utils.showToast(getString(R.string.easter_egg_discovered))
                                prefs.easterEggDiscovered = true
                            } else Utils.showToast(getString(R.string.easter_egg_tease))
                        }
                    }
                )

                SettingItem(
                    icon = {
                        Icon(
                            Icons.Outlined.HelpOutline,
                            contentDescription = null
                        )
                    },
                    text = { Text(stringResource(R.string.help)) },
                    secondaryText = { Text(stringResource(R.string.help_description)) },
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://discord.gg/c3dJ2t7KRW")
                    }
                )

                SettingItem(
                    icon = {
                        Icon(
                            Icons.Outlined.Feed,
                            contentDescription = stringResource(R.string.license)
                        )
                    },
                    text = { Text(stringResource(R.string.license)) },
                    secondaryText = { Text("GNU General Public License v2.0") }
                )

                SettingItem(
                    icon = {
                        Icon(
                            Icons.Filled.FavoriteBorder,
                            contentDescription = ""
                        )
                    },
                    text = { Text(stringResource(R.string.support_me)) },
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://github.com/sponsors/wingio")
                    }
                )

                SettingItem(
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_code_24),
                            contentDescription = stringResource(R.string.source)
                        )
                    },
                    text = { Text(stringResource(R.string.source)) },
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://github.com/wingio/Logra")
                    }
                )
            }
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Toolbar() {
        val navigator = LocalNavigator.current

        LargeTopAppBar(
            title = { Text(stringResource(R.string.about)) },
            navigationIcon = {
                IconButton(onClick = { navigator?.pop() }) {
                    Icon(Icons.Filled.ArrowBack, stringResource(R.string.back))
                }
            }
        )
    }

}