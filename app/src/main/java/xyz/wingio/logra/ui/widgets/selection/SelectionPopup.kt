package xyz.wingio.logra.ui.widgets.selection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import xyz.wingio.logra.R
import xyz.wingio.logra.domain.logcat.LogcatEntry
import xyz.wingio.logra.utils.Utils.saveText
import xyz.wingio.logra.utils.Utils.shareText
import java.text.SimpleDateFormat
import java.util.*

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun SelectionPopup(
    selected: List<LogcatEntry> = emptyList(),
    onDismissRequest: (() -> Unit)?
) {
    val ctx = LocalContext.current

    AnimatedVisibility(
        visible = selected.isNotEmpty(),
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        Popup(
            alignment = Alignment.BottomCenter,
            properties = PopupProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = true
            ),
            onDismissRequest = onDismissRequest
        ) {
            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(20.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pluralStringResource(
                                R.plurals.selected,
                                selected.size,
                                selected.size
                            ), style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear_24),
                            contentDescription = stringResource(id = R.string.clear_slected),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onDismissRequest?.invoke()
                                }
                        )
                    }
                    Row(
                        Modifier.background(Color(0x32000000))
                    ) {
                        SelectionPopupOption(icon = {
                            Icon(
                                Icons.Filled.Share,
                                stringResource(id = R.string.share)
                            )
                        }) {
                            ctx.shareText(
                                selected
                                    .sortedBy { it.createdAt }
                                    .joinToString("\n") {
                                        it.raw
                                    }
                            )
                        }
                        SelectionPopupOption(icon = {
                            Icon(
                                painterResource(id = R.drawable.ic_save_24), stringResource(
                                    id = R.string.save
                                )
                            )
                        }) {
                            ctx.saveText(
                                selected
                                    .sortedBy { it.createdAt }
                                    .joinToString("\n") {
                                        it.raw
                                    },
                                "Logcat ${SimpleDateFormat("M/dd/yy H:mm:ss.SSS").format(Date())}"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.SelectionPopupOption(
    enabled: Boolean = true,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(enabled = enabled, onClick = onClick)
            .padding(20.dp)
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        val contentColor =
            if (enabled) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.38f)
        CompositionLocalProvider(LocalContentColor provides contentColor, content = icon)
    }
}