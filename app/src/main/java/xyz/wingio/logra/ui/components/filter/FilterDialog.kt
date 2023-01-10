package xyz.wingio.logra.ui.components.filter

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import xyz.wingio.logra.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputDialog(
    visible: Boolean = false,
    @StringRes label: Int = R.string.text,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {

    var text by remember { mutableStateOf("") }

    if (visible) AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = { onConfirm(text); onDismissRequest() }) {
                Text(stringResource(R.string.confirm))
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(id = label)) }
                )
            }
        }
    )
}