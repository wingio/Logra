package xyz.wingio.logra.ui.widgets.logs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.get
import xyz.wingio.logra.domain.logcat.LogcatEntry
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.utils.Utils.addUnique
import java.text.SimpleDateFormat

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogEntry(
    log: LogcatEntry,
    prefs: PreferenceManager = get(),
    selected: MutableList<LogcatEntry> = mutableListOf(),
    demoColor: Color? = null
) {
    val isSelected = selected.contains(log)
    val logColor = demoColor ?: log.level.color

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithCache {
                    onDrawBehind {
                        drawRect(
                            color = logColor,
                            topLeft = Offset.Zero,
                            size = Size(12f, size.height),
                        )
                    }
                }
                .combinedClickable(onLongClick = {
                    if (selected.isEmpty()) selected.addUnique(log)
                }) {
                    if (isSelected)
                        selected.remove(log)
                    else if (selected.isNotEmpty())
                        selected.addUnique(log)
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        log.tag,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp))
                            .padding(5.dp)
                    )

                    Text(
                        modifier = if (!prefs.lineWrap) Modifier.horizontalScroll(
                            rememberScrollState()
                        ) else Modifier,
                        text = log.content,
                        style = MaterialTheme.typography.labelMedium,
                        softWrap = prefs.lineWrap
                    )
                    Text(
                        text = SimpleDateFormat(prefs.timestampFormat).format(log.createdAt),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontStyle = FontStyle.Italic,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
    Spacer(
        modifier = Modifier
            .height(10.dp)
            .fillMaxWidth()
    )
}