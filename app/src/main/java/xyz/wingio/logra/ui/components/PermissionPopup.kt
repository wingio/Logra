package xyz.wingio.logra.ui.components

import android.Manifest
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import xyz.wingio.logra.BuildConfig
import xyz.wingio.logra.utils.*

@Composable
fun PermissionPopup() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    AlertDialog(
        text = {
            Text(
                text = "This app requires a special permission called \"Read logs\" to be able to function.\n" +
                        "You can use root, Shizuku, or grant it manually via adb.\n" +
                        "If you have root, press the \"Use root\" button below\n" +
                        "If not, Shizuku (https://shizuku.rikka.app/) can also be used.\n" +
                        "If neither of those options work, you can manually grant it via adb with the command \"adb shell pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.READ_LOGS}\".",
                style = MaterialTheme.typography.body1
            )
        },
        buttons = {
            TextButton(
                onClick = {
                    // Root
                    coroutineScope.launch {
                        val grantedRoot = checkRootPermission()
                        if (!grantedRoot) with(context) {
                            Utils.showToast("Error granting permission with root, please try again.")
                        } else {
                            AlertDialog(
                                text = {
                                    Text(
                                        text = "Once granted, the app will close. Press the button below to grant the permission and restart the app.",
                                        style = MaterialTheme.typography.body1
                                    )
                                },
                                buttons = {
                                    TextButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                grantPermissionsWithRoot()
                                            }
                                        },
                                        content = {
                                            Text(
                                                text = "Grant and close",
                                                style = MaterialTheme.typography.button
                                            )
                                        }
                                    )
                                },
                                onDismissRequest = {}
                            )
                        }
                    }
                },
                content = {
                    Text(
                        "Grant via root",
                        style = MaterialTheme.typography.button
                    )
                }
            )
            TextButton(
                onClick = {
                    // Shizuku
                    coroutineScope.launch {
                        val shizukuGranted = checkShizukuPermission()
                        if (!shizukuGranted) with(context) {
                            Utils.showToast("Error granting permission with Shizuku, please try again.")
                        } else {
                            AlertDialog(
                                text = {
                                    Text(
                                        text = "Once granted, the app will close. Press the button below to grant the permission and restart the app.",
                                        style = MaterialTheme.typography.body1
                                    )
                                },
                                buttons = {
                                    TextButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                grantPermissionsWithShizuku()
                                            }
                                        },
                                        content = {
                                            Text(
                                                text = "Grant and close",
                                                style = MaterialTheme.typography.button
                                            )
                                        }
                                    )
                                },
                                onDismissRequest = {}
                            )
                        }
                    }
                },
                content = {
                    Text(
                        "Grant via shizuku",
                        style = MaterialTheme.typography.button
                    )
                }
            )
        },
        onDismissRequest = {}
    )
}