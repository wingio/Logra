package xyz.wingio.logra.ui.screens.crashes

import android.content.pm.ApplicationInfo
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import xyz.wingio.logra.R
import xyz.wingio.logra.crashdetector.db.entities.Crash
import xyz.wingio.logra.domain.manager.PreferenceManager
import xyz.wingio.logra.ui.viewmodels.crashes.CrashesViewModel
import xyz.wingio.logra.utils.Utils.copyText
import xyz.wingio.logra.utils.Utils.shareText
import xyz.wingio.logra.utils.initCrashService
import xyz.wingio.logra.utils.stopCrashService
import java.text.SimpleDateFormat
import java.util.*

class CrashesScreen : Screen {

    @Composable
    override fun Content() = Screen()

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun Screen(
        viewModel: CrashesViewModel = getScreenModel()
    ) {
        val nav = LocalNavigator.current
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        if (nav?.lastEvent == StackEvent.Pop) viewModel.getCrashes()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { TopBar(scrollBehavior, viewModel) },
        ) { pv ->
            LazyColumn(
                modifier = Modifier
                    .padding(pv)
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    EnabledSwitch()
                }
                item {
                    Spacer(modifier = Modifier.height(7.dp))
                }
                items(viewModel.crashes) {
                    CrashCard(crash = it, viewModel = viewModel)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar(
        scrollBehavior: TopAppBarScrollBehavior,
        viewModel: CrashesViewModel
    ) {
        val nav = LocalNavigator.current

        LargeTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.crashes))
            },
            navigationIcon = {
                IconButton(onClick = { nav?.pop() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(
                            id = R.string.back
                        )
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.clearCrashes() }) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = stringResource(id = R.string.clear)
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    @Composable
    private fun EnabledSwitch(
        prefs: PreferenceManager = get()
    ) {
        val ctx = LocalContext.current
        val scope = rememberCoroutineScope()

        fun toggleCDEnabled(enabled: Boolean) {
            prefs.crashDetectorEnabled = enabled

            with(ctx) {
                if (enabled)
                    initCrashService()
                else
                    scope.launch {
                        delay(1000)
                        stopCrashService()
                    }
            }
        }

        Card {
            Box(
                Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable {
                        toggleCDEnabled(!prefs.crashDetectorEnabled)
                    }
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(24.dp, 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        ProvideTextStyle(
                            MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(text = "Crash Detector")
                        }
                        ProvideTextStyle(
                            MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary.copy(0.6f)
                            )
                        ) {
                            Text(text = "Disabling will close the app")
                        }
                    }

                    Spacer(Modifier.weight(1f, true))

                    Switch(
                        checked = prefs.crashDetectorEnabled,
                        onCheckedChange = ::toggleCDEnabled,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            checkedThumbColor = MaterialTheme.colorScheme.secondary,
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }
            }

        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun CrashCard(
        crash: Crash,
        viewModel: CrashesViewModel
    ) {
        val app: ApplicationInfo?
        val ctx = LocalContext.current
        val nav = LocalNavigator.current
        val prefs: PreferenceManager = get()
        with(ctx) {
            app = crash.getApp()
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .clickable {
                        nav?.push(CrashDetailScreen(crash))
                    }
                    .padding(start = 15.dp, end = 15.dp, top = 15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 15.dp)
                ) {
                    app?.loadIcon(ctx.packageManager)?.toBitmap()?.asImageBitmap()
                        ?.let {
                            Image(
                                bitmap = it,
                                contentDescription = app.loadLabel(ctx.packageManager).toString(),
                                modifier = Modifier.size(35.dp)
                            )
                        }

                    Column {
                        Text(
                            "${app?.loadLabel(ctx.packageManager) ?: "???"} (${crash.packageName})",
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = SimpleDateFormat(prefs.timestampFormat).format(Date(crash.time)),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontStyle = FontStyle.Italic,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontFamily = FontFamily.Monospace
                            )
                        ) {
                            append(crash.stacktrace)
                        }
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp))
                        .padding(10.dp)
                        .horizontalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.labelSmall
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 3.dp, bottom = 5.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { viewModel.deleteCrash(crash.id) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(text = stringResource(id = R.string.delete))
                    }
                    TextButton(onClick = { ctx.shareText(crash.stacktrace) }) {
                        Text(text = stringResource(id = R.string.share))
                    }
                    TextButton(onClick = { ctx.copyText(crash.stacktrace) }) {
                        Text(text = stringResource(id = R.string.copy))
                    }
                }
            }
        }
    }

}