package xyz.wingio.logra.ui.components.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun DropdownScope.DropdownItem(
    text: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val index = remember {
        items.add(Random.nextInt())
        items.lastIndex
    }

    val background = MaterialTheme.colorScheme.background.copy(alpha = 0.8f).compositeOver(
        MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    )

    val shape = when (index) {
        0 -> RoundedCornerShape(
            topStart = 15.dp,
            topEnd = 15.dp,
            bottomStart = 4.dp,
            bottomEnd = 4.dp
        )

        items.lastIndex -> RoundedCornerShape(
            bottomStart = 15.dp,
            bottomEnd = 15.dp,
            topStart = 4.dp,
            topEnd = 4.dp
        )

        else -> RoundedCornerShape(4.dp)
    }

    Row(
        modifier = Modifier
            .shadow(3.dp, shape, clip = false)
            .clip(shape)
            .background(background)
            .run {
                if (enabled) clickable { onClick?.invoke() } else this
            }
            .padding(16.dp)
            .widthIn(min = 100.dp)
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.labelLarge.copy(
                color = LocalContentColor.current.copy(
                    alpha = if (enabled) 1f else 0.5f
                )
            ),
        ) {
            text()
        }
    }
}