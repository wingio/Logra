package xyz.wingio.logra.ui.widgets.settings.colors

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import xyz.wingio.logra.domain.manager.Theme
import xyz.wingio.logra.ui.components.BottomSheet
import xyz.wingio.logra.ui.components.ThemeWrapper
import xyz.wingio.logra.ui.components.colorpicker.ColorPicker
import xyz.wingio.logra.ui.components.tabs.Tab
import xyz.wingio.logra.ui.components.tabs.TabAlignment
import xyz.wingio.logra.ui.components.tabs.TabRow
import xyz.wingio.logra.ui.widgets.logs.LogEntry
import xyz.wingio.logra.utils.DEMO_LOG

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColorSheet(
    defaultLight: Color,
    defaultDark: Color,
    onDismissRequest: () -> Unit,
    onColorsChanged: (Color, Color) -> Unit
) {
    var previewTheme by remember {
        mutableStateOf(Theme.LIGHT)
    }
    val darkTheme = previewTheme == Theme.DARK

    var lightColor by remember {
        mutableStateOf(defaultLight)
    }
    var darkColor by remember {
        mutableStateOf(defaultDark)
    }
    val color = if (darkTheme) darkColor else lightColor

    BottomSheet(
        onDismissRequest = {
            onColorsChanged(lightColor, darkColor)
            onDismissRequest()
        },
        properties = BottomSheetDialogProperties(navigationBarColor = MaterialTheme.colorScheme.surface),
        title = {
            Text(text = "Edit Color")
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TabRow(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Tab(
                    label = {
                        Text(text = "Light")
                    },
                    selected = !darkTheme,
                    align = TabAlignment.START
                ) {
                    previewTheme = Theme.LIGHT
                }
                Tab(
                    label = {
                        Text(text = "Dark")
                    },
                    selected = darkTheme,
                    align = TabAlignment.END
                ) {
                    previewTheme = Theme.DARK
                }
            }



            AnimatedContent(
                targetState = previewTheme,
                transitionSpec = {
                    val direction = when (targetState) {
                        Theme.DARK -> AnimatedContentScope.SlideDirection.Left
                        Theme.LIGHT -> AnimatedContentScope.SlideDirection.Right
                        else -> AnimatedContentScope.SlideDirection.Left
                    }
                    slideIntoContainer(direction) with slideOutOfContainer(direction)
                }
            ) {
                Column {
                    ColorPicker(
                        defaultColor = color
                    ) {
                        if (darkTheme)
                            darkColor = it
                        else
                            lightColor = it
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ThemeWrapper(previewTheme) {
                        Surface(
                            modifier = Modifier.clip(RoundedCornerShape(18.dp))
                        ) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                Box(
                                    Modifier.sizeIn(maxHeight = 95.dp)
                                ) {
                                    LogEntry(log = DEMO_LOG, demoColor = color)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}