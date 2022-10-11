package xyz.wingio.logra.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties

@Composable
fun BottomSheet(
    onDismissRequest: () -> Unit,
    properties: BottomSheetDialogProperties = BottomSheetDialogProperties(),
    title: @Composable () -> Unit = {},
    subtitle: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    BottomSheetDialog(onDismissRequest, properties) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.headlineMedium) {
                    title()
                }
                ProvideTextStyle(value = MaterialTheme.typography.titleSmall) {
                    subtitle()
                }
                Box(modifier = Modifier.padding(top = 10.dp)) {
                    content()
                }
            }
        }
    }
}