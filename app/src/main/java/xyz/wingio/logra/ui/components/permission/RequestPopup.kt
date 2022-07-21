package xyz.wingio.logra.ui.components.permission

import android.Manifest
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.launch
import xyz.wingio.logra.BuildConfig
import xyz.wingio.logra.utils.ShizukuRequestResult
import xyz.wingio.logra.utils.Utils
import xyz.wingio.logra.utils.checkShizukuPermission
import xyz.wingio.logra.utils.grantPermissionsWithRoot
import xyz.wingio.logra.utils.logcat.LogcatManager

@OptIn(ExperimentalTextApi::class)
@Composable
fun RequestPopup(onDialogChange: (PopupState) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        text = {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                    append(
                        "This app requires a special permission called \"Read logs\" to be able to function.\n" +
                                "You can use root, Shizuku, or grant it manually via adb.\n" +
                                "If you have root, press the \"Use root\" button below, and accept the superuser request.\n" +
                                "If not, "
                    )
                    withStyle(
                        style = SpanStyle(color = MaterialTheme.colorScheme.primary)
                    ) {
                        withAnnotation(
                            tag = "URL",
                            annotation = "https://shizuku.rikka.app/",
                        ) {
                            append("Shizuku")
                        }
                    }
                    append(
                        " can also be used.\n" +
                                "If neither of those options work, " +
                                "you can manually grant it via adb with " +
                                "the following command:\n"
                    )
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        append("adb shell pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.READ_LOGS}")
                    }
                    append(".")
                }
            }

            val uriHandler = LocalUriHandler.current

            ClickableText(
                text = annotatedString,
                onClick = { pos ->
                    annotatedString.getStringAnnotations("URL", pos, pos).firstOrNull()
                        ?.let { stringAnnotation ->
                            uriHandler.openUri(stringAnnotation.item)
                        }
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Root
                    coroutineScope.launch {
                        grantPermissionsWithRoot().also {
                            LogcatManager.connect()
                            onDialogChange(PopupState.NONE)
                        }
                    }
                },
                content = {
                    Text(
                        "Grant via root"
                    )
                }
            )
            TextButton(
                onClick = {
                    // Shizuku
                    coroutineScope.launch {
                        val shizukuRequestResult = checkShizukuPermission()
                        with(context) {
                            when (shizukuRequestResult) {
                                ShizukuRequestResult.GRANTED -> onDialogChange(PopupState.SHIZUKU)
                                ShizukuRequestResult.DENIED -> Utils.showToast("Shizuku permission must be accepted in order to use it.")
                                ShizukuRequestResult.PRE_V11 -> Utils.showToast("Your Shizuku is outdated, please update it.")
                                ShizukuRequestResult.NOT_RUNNING -> Utils.showToast("Shizuku service does not seem to be running, please start it first.")
                                ShizukuRequestResult.OUTDATED_SDK -> Utils.showToast("Shizuku method is only available on android 6 and later.")
                            }
                        }
                    }
                },
                content = {
                    Text(
                        "Grant via shizuku"
                    )
                }
            )
        },
        onDismissRequest = {
            onDialogChange(PopupState.NONE)
        }
    )
}