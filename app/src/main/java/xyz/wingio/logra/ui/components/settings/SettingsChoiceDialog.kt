package xyz.wingio.logra.ui.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import xyz.wingio.logra.R
import xyz.wingio.logra.ui.components.EnumRadioController

@Composable
inline fun <reified E : Enum<E>> SettingsChoiceDialog(
    visible: Boolean = false,
    noinline onRequestClose: (E) -> Unit = {},
    noinline title: @Composable () -> Unit,
    crossinline description: @Composable () -> Unit = {},
    default: E
) {

    var choice by remember { mutableStateOf(default) }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        AlertDialog(
            onDismissRequest = { onRequestClose(choice) },
            title = title,
            text = {
                description()
                EnumRadioController(default) { choice = it }
            },
            confirmButton = {
                FilledTonalButton(onClick = { onRequestClose(choice) }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        )
    }

}
