package xyz.wingio.logra.ui.components.tabs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun TabRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        Modifier
            .clip(CircleShape)
            .border(2.dp, SolidColor(MaterialTheme.colorScheme.primary), CircleShape)
            .then(modifier)
    ) {
        content()
    }
}