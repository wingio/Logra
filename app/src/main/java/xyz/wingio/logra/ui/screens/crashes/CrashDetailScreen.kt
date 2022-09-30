package xyz.wingio.logra.ui.screens.crashes

import android.app.Activity
import android.content.pm.ApplicationInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.androidx.compose.get
import xyz.wingio.logra.R
import xyz.wingio.logra.crashdetector.db.entities.Crash
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.ui.viewmodels.crashes.CrashDetailViewModel
import xyz.wingio.logra.utils.Utils.copyText
import xyz.wingio.logra.utils.Utils.saveText
import xyz.wingio.logra.utils.Utils.shareText
import java.text.SimpleDateFormat
import java.util.*

class CrashDetailScreen(private val crash: Crash) : Screen {

    @Composable
    override fun Content() = Screen()

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun Screen(
        viewModel: CrashDetailViewModel = getScreenModel(),
        prefs: PreferenceManager = get()
    ) {
        val ctx = LocalContext.current
        val navigator = LocalNavigator.current
        var app: ApplicationInfo?
        val pm = ctx.packageManager
        with(ctx) {
            app = crash.getApp()
        }

        Scaffold(
            topBar = { TopBar() }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SelectionContainer {
                    Text(
                        crash.stacktrace,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp))
                            .padding(10.dp)
                            .horizontalScroll(rememberScrollState()),
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        bitmap = app?.loadIcon(pm)?.toBitmap()?.asImageBitmap() ?: ImageBitmap(
                            24,
                            24
                        ),
                        contentDescription = app?.loadLabel(pm).toString(),
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = stringResource(
                            id = R.string.crash_info,
                            app?.loadLabel(pm).toString(),
                            crash.packageName,
                            SimpleDateFormat(prefs.timestampFormat).format(Date(crash.time))
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontStyle = FontStyle.Italic,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    OutlinedIconButton(
                        onClick = {
                            viewModel.deleteCrash(crash.id)
                            if (navigator?.pop() != true) (ctx as? Activity)?.finish()
                        },
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(
                                MaterialTheme.colorScheme.error
                            )
                        ),
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete, contentDescription = stringResource(
                                id = R.string.delete
                            ), modifier = Modifier.padding(10.dp)
                        )
                    }
                    OutlinedIconButton(onClick = { ctx.copyText(crash.stacktrace) }) {
                        Icon(
                            imageVector = Icons.Filled.CopyAll, contentDescription = stringResource(
                                id = R.string.copy
                            ), modifier = Modifier.padding(10.dp)
                        )
                    }
                    OutlinedButton(onClick = { ctx.shareText(crash.stacktrace) }) {
                        Text(text = stringResource(id = R.string.share))
                    }
                    FilledTonalButton(onClick = {
                        ctx.saveText(
                            crash.stacktrace,
                            "Crash (${crash.packageName}) ${
                                SimpleDateFormat("M/dd/yy H:mm:ss.SSS").format(Date(crash.time))
                            }"
                        )
                    }) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }
            }
        }
    }

    @Composable
    private fun TopBar() {
        val ctx = LocalContext.current
        val navigator = LocalNavigator.current

        LargeTopAppBar(
            title = {
                Text(stringResource(id = R.string.crash))
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (navigator?.pop() != true) (ctx as? Activity)?.finish()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(
                            id = R.string.back
                        )
                    )
                }
            }
        )
    }

}