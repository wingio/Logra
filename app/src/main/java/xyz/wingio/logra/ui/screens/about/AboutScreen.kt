package xyz.wingio.logra.ui.screens.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import xyz.wingio.logra.BuildConfig
import xyz.wingio.logra.R
import xyz.wingio.logra.ui.components.settings.SettingItem
import xyz.wingio.logra.utils.Utils

class AboutScreen : Screen {

    @Composable
    override fun Content() = Screen()

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun Screen() {
        val ctx = LocalContext.current
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
                        with(ctx) {
                            Utils.showToast("No secrets here!")
                        }
                    }
                )
                val uriHandler = LocalUriHandler.current
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