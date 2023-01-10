package xyz.wingio.logra.ui.widgets.selection

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import xyz.wingio.logra.R
import xyz.wingio.logra.ui.components.dropdown.Dropdown
import xyz.wingio.logra.ui.components.dropdown.DropdownItem

@Composable
fun SearchByPopup(
    expanded: Boolean,
    offset: DpOffset = DpOffset.Unspecified,
    onDismissRequest: () -> Unit = {},
    onTextClick: () -> Unit = {},
    onTagClick: () -> Unit = {},
    onPIDClick: () -> Unit = {}
) {
    Dropdown(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset,
        origin = TransformOrigin(0.5f, 1f)
    ) {
        DropdownItem(
            text = { Text(stringResource(R.string.text)) },
            onClick = {
                onDismissRequest()
                onTextClick()
            }
        )
        DropdownItem(
            text = { Text(stringResource(R.string.tag)) },
            onClick = {
                onDismissRequest()
                onTagClick()
            }
        )
        DropdownItem(
            text = { Text(stringResource(R.string.pid)) },
            onClick = {
                onDismissRequest()
                onPIDClick()
            }
        )
    }
}