package xyz.wingio.logra.ui.components.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import xyz.wingio.logra.R
import xyz.wingio.logra.domain.logcat.filter.Filter

@Composable
fun TagInputDialog(
    modifier: Modifier = Modifier,
    currentFilter: Filter? = null,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val filter = currentFilter ?: Filter()

    var text by remember { mutableStateOf(filter.tag) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = { onConfirm(text); onDismissRequest() }) {
                Text(stringResource(R.string.confirm))
            }
        },
        title = { Text(stringResource(R.string.tag)) },
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