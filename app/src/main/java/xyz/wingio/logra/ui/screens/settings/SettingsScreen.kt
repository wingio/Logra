package xyz.wingio.logra.ui.screens.settings

import android.os.Build
import android.text.style.ClickableSpan
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.koin.androidx.compose.get
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.ui.components.settings.SettingsSwitch
import xyz.wingio.logra.ui.components.settings.SettingsTextField
import java.text.SimpleDateFormat
import java.util.*

class SettingsScreen : Screen, KoinComponent {



    @Composable
    override fun Content() = Screen()

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun Screen() {
        val prefs: PreferenceManager = get()

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

                Divider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outline
                )

                SettingsTextField(label = "Timestamp Format", pref = prefs.timestampFormat, onPrefChange = {
                    try {
                        SimpleDateFormat(it).format(Date(System.currentTimeMillis()))
                        prefs.timestampFormat = it
                    } catch (e: Throwable) {}
                } )

                val uriHandler = LocalUriHandler.current
                val text = formatInfo

                ClickableText(
                    text = text,
                    onClick = { pos ->
                        text.getStringAnnotations("URL", pos, pos).firstOrNull()?.let { stringAnnotation ->
                            uriHandler.openUri(stringAnnotation.item)
                        }
                    },
                    style = MaterialTheme.typography.labelSmall.copy( color = MaterialTheme.colorScheme.onSurface ),
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
                )
            }
        }
    }


    @OptIn(ExperimentalTextApi::class)
    val formatInfo
        @Composable get() = buildAnnotatedString {
            val prefs: PreferenceManager = get()
            append("""
                Current date: ${SimpleDateFormat(prefs.timestampFormat).format(Date(System.currentTimeMillis()))}
                
                Learn more about date formats 
            """.trimIndent())
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
    private fun Toolbar() {
        LargeTopAppBar(
            title = { Text("Settings") }
        )
    }

}