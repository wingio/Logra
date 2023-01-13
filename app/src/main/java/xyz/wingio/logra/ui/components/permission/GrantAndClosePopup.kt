package xyz.wingio.logra.ui.components.permission

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import xyz.wingio.logra.R
import xyz.wingio.logra.utils.grantPermissionsWithShizuku

@Composable
fun GrantAndClosePopup(onDialogChange: (PopupState) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        text = {
            Text(
                text = stringResource(R.string.confirm_grant_restart),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        grantPermissionsWithShizuku()
                    }
                },
                content = {
                    Text(
                        text = stringResource(R.string.grant_and_close)
                    )
                }
            )
        },
        onDismissRequest = {
            onDialogChange(PopupState.NONE)
        }
    )
}

enum class GrantMethod {
    ROOT,
    SHIZUKU
}