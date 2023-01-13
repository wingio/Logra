package xyz.wingio.logra.ui.screens.settings

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.androidx.compose.get
import xyz.wingio.logra.R
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.ui.components.settings.*
import xyz.wingio.logra.ui.screens.about.AboutScreen
import java.text.SimpleDateFormat
import java.util.*

class SettingsScreen : Screen {

    @Composable
    override fun Content() = Screen()

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun Screen(
        prefs: PreferenceManager = get()
    ) {
        val navigator = LocalNavigator.current
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Scaffold(
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { Toolbar(scrollBehavior) }
        ) { paddingValues ->
            Column(
                Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {

                SettingsHeader(stringResource(R.string.settings_general))

                SettingsSwitch(label = stringResource(R.string.settings_line_wrap), pref = prefs.lineWrap) { prefs.lineWrap = it }
                SettingsSwitch(label = stringResource(R.string.settings_compact_mode), pref = prefs.compact) { prefs.compact = it }
                SettingsSwitch(
                    label = stringResource(R.string.settings_dump_logs),
                    secondaryLabel = stringResource(R.string.settings_dump_logs_subtext),
                    pref = prefs.dumpLogs
                ) { prefs.dumpLogs = it }

                SettingsHeader(stringResource(R.string.settings_appearance))

                SettingsSwitch(
                    label = stringResource(R.string.settings_monet),
                    pref = prefs.monet,
                    disabled = Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                ) { prefs.monet = it }

                SettingsItemChoice(
                    label = stringResource(R.string.settings_theme),
                    pref = prefs.theme,
                    onPrefChange = { prefs.theme = it })

                SettingItem(
                    modifier = Modifier.clickable {
                        navigator?.push(ColorSettingsScreen())
                    },
                    text = {
                        Text(text = stringResource(R.string.settings_colors))
                    }
                ) {
                    Icon(Icons.Filled.ChevronRight, "")
                }

                SettingItem(
                    modifier = Modifier.clickable {
                        navigator?.push(IconSettingsScreen())
                    },
                    text = {
                        Text(text = stringResource(R.string.settings_app_icon))
                    }
                ) {
                    Icon(Icons.Filled.ChevronRight, "")
                }

                SettingsHeader(stringResource(R.string.settings_advanced))

                SettingsTextField(
                    label = stringResource(R.string.settings_timestamp),
                    pref = prefs.timestampFormat,
                    onPrefChange = {
                        try {
                            SimpleDateFormat(it).format(Date(System.currentTimeMillis()))
                            prefs.timestampFormat = it
                        } catch (e: Throwable) {
                        }
                    })

                val uriHandler = LocalUriHandler.current
                val text = formatInfo

                ClickableText(
                    text = text,
                    onClick = { pos ->
                        text.getStringAnnotations("URL", pos, pos).firstOrNull()
                            ?.let { stringAnnotation ->
                                uriHandler.openUri(stringAnnotation.item)
                            }
                    },
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
                )
            }
        }
    }


    @OptIn(ExperimentalTextApi::class)
    val formatInfo
        @Composable get() = buildAnnotatedString {
            val prefs: PreferenceManager = get()
            append(
                """
                Current date: ${SimpleDateFormat(prefs.timestampFormat).format(Date(System.currentTimeMillis()))}
                
                Learn more about date formats 
            """.trimIndent()
            )
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                withAnnotation(
                    tag = "URL",
                    annotation = "https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html",
                ) {
                    append("here")
                }
            }
        }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun Toolbar(
        scrollBehavior: TopAppBarScrollBehavior
    ) {

        val navigator = LocalNavigator.current

        LargeTopAppBar(
            title = { Text(stringResource(R.string.settings)) },
            scrollBehavior = scrollBehavior,
            actions = {
                IconButton(onClick = { navigator?.push(AboutScreen()) }) {
                    Icon(Icons.Outlined.Info, stringResource(R.string.about))
                }
            },
            navigationIcon = {
                IconButton(onClick = { navigator?.pop() }) {
                    Icon(Icons.Filled.ArrowBack, stringResource(R.string.back))
                }
            }
        )
    }

}