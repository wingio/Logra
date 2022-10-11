package xyz.wingio.logra.ui.widgets.settings.colors

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.wingio.logra.domain.manager.Theme
import xyz.wingio.logra.ui.components.ThemeWrapper
import xyz.wingio.logra.ui.components.settings.SettingItem

@Composable
fun ColorOption(
    lightColor: Color,
    darkColor: Color,
    label: @Composable () -> Unit,
    onColorsChanged: (Color, Color) -> Unit
) {
    var colorEditorOpened by remember {
        mutableStateOf(false)
    }

    var dark by remember {
        mutableStateOf(darkColor)
    }
    var light by remember {
        mutableStateOf(lightColor)
    }

    SettingItem(
        Modifier.clickable {
            colorEditorOpened = true
        },
        text = label
    ) {
        Row(
            Modifier
                .clip(CircleShape)
                .clickable {
                    colorEditorOpened = true
                }
        ) {
            ThemeWrapper(Theme.LIGHT) {
                val surface = MaterialTheme.colorScheme.surface
                Canvas(
                    Modifier
                        .size(26.dp, 52.dp)
                        .clipToBounds()
                ) {
                    drawCircle(
                        color = surface,
                        radius = 26.dp.toPx(),
                        center = Offset(x = 26.dp.toPx(), y = size.height / 2)
                    )
                    drawCircle(
                        color = light,
                        radius = 18.dp.toPx(),
                        center = Offset(x = 26.dp.toPx(), y = size.height / 2)
                    )
                }
            }
            ThemeWrapper(Theme.DARK) {
                val surface = MaterialTheme.colorScheme.surface
                Canvas(
                    Modifier
                        .size(26.dp, 52.dp)
                        .clipToBounds()
                ) {
                    drawCircle(
                        color = surface,
                        radius = 26.dp.toPx(),
                        center = Offset(x = 0f, y = size.height / 2)
                    )
                    drawCircle(
                        color = dark,
                        radius = 18.dp.toPx(),
                        center = Offset(x = 0f, y = size.height / 2)
                    )
                }
            }
        }
    }

    if (colorEditorOpened) ColorSheet(
        onDismissRequest = { colorEditorOpened = false },
        defaultLight = lightColor,
        defaultDark = darkColor
    ) { l, d ->
        light = l
        dark = d
        onColorsChanged(light, dark)
    }

}