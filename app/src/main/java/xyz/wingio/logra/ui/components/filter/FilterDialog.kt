package xyz.wingio.logra.ui.components.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import xyz.wingio.logra.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagInputDialog(
    visible: Boolean = false,
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
//        title = { Text(stringResource(R.string.tag)) },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(id = R.string.tag)) }
                )
            }
        }
    )
}