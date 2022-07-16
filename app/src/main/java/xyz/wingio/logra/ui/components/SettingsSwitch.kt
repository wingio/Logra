package xyz.wingio.logra.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsSwitch(
    modifier: Modifier = Modifier,
    label: String,
    disabled: Boolean = false,
    pref: Boolean,
    onPrefChange: (Boolean) -> Unit,
) {
    SettingItem(
        modifier = Modifier.clickable(enabled = !disabled) { onPrefChange(!pref) },
        text = { Text(text = label) },
    ) {
        Switch(
            checked = pref,
            enabled = !disabled,
            onCheckedChange = { onPrefChange(!pref) }
        )
    }
}