package xyz.wingio.logra.ui.components.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun Tab(
    label: @Composable () -> Unit,
    selected: Boolean,
    align: TabAlignment = TabAlignment.MIDDLE,
    onTabSelected: () -> Unit = {}
) {
    Row(
        Modifier
            .clickable {
                onTabSelected()
            }
            .let {
                if (selected) it.background(MaterialTheme.colorScheme.primary) else it
            }
            .border(
                if (selected) 0.dp else 1.dp,
                SolidColor(MaterialTheme.colorScheme.primary),
                RectangleShape
            )
            .padding(vertical = 8.dp, horizontal = 20.dp)
    ) {
        if (align == TabAlignment.START) Spacer(modifier = Modifier.width(2.dp))
        ProvideTextStyle(value = MaterialTheme.typography.labelLarge.let {
            if (selected) it.copy(color = MaterialTheme.colorScheme.onPrimary) else it
        }) {
            label()
        }
        if (align == TabAlignment.END) Spacer(modifier = Modifier.width(2.dp))
    }
}

enum class TabAlignment {
    START,
    MIDDLE,
    END
}

