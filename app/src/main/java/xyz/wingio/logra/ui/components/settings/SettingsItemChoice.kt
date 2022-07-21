package xyz.wingio.logra.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import xyz.wingio.logra.utils.Utils.capitalizedName

@Composable
inline fun <reified E : Enum<E>> SettingsItemChoice(
    modifier: Modifier = Modifier,
    label: String,
    title: String = label,
    disabled: Boolean = false,
    pref: E,
    crossinline onPrefChange: (E) -> Unit,
) {

    var opened = remember {
        mutableStateOf(false)
    }

    SettingItem(
        modifier = Modifier.clickable { opened.value = true },
        text = { Text(text = label) },
    ) {
        SettingsChoiceDialog(
            visible = opened.value,
            title = { Text(title) },
            default = pref,
            onRequestClose = {
                opened.value = false
                onPrefChange(it)
            }
        )
        FilledTonalButton(onClick = { opened.value = true }, enabled = !disabled) {
            Text(pref.capitalizedName)
        }
    }
}