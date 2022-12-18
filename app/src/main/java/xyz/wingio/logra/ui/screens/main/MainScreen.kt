package xyz.wingio.logra.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xyz.wingio.logra.R
import xyz.wingio.logra.ui.components.filter.FilterRow
import xyz.wingio.logra.ui.components.filter.RoundedTextBox
import xyz.wingio.logra.ui.screens.crashes.CrashesScreen
import xyz.wingio.logra.ui.screens.settings.SettingsScreen
import xyz.wingio.logra.ui.theme.logLineAlt
import xyz.wingio.logra.ui.viewmodels.main.MainScreenViewModel
import xyz.wingio.logra.ui.widgets.logs.LogEntry
import xyz.wingio.logra.ui.widgets.selection.SelectionPopup
import xyz.wingio.logra.utils.Utils.saveText
import java.text.SimpleDateFormat
import java.util.*

class MainScreen : Screen {

    @Composable
    override fun Content() = Screen()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Screen(
        viewModel: MainScreenViewModel = getScreenModel()
    ) {
        val listState = rememberLazyListState()
        val logs by remember {
            derivedStateOf {
                viewModel.filterLogs()
            }
        }

        LaunchedEffect(Unit) {
            listState.interactionSource.interactions.collectLatest {
                if (it is DragInteraction.Start) viewModel.freeScroll = true
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { Toolbar(viewModel) },
            floatingActionButton = { JumpFAB(viewModel, listState, logs.lastIndex) }
        ) { pad ->

            LaunchedEffect(logs) {
                if (logs.isNotEmpty() && !viewModel.freeScroll) {
                    listState.animateScrollToItem(logs.lastIndex)
                }
            }

            SelectionPopup(viewModel.selectedLogs) {
                viewModel.selectedLogs.clear()
            }

            Column(
                Modifier
                    .padding(pad)
            ) {
                AnimatedVisibility(visible = viewModel.filterOpened) {
                    FilterRow(filter = viewModel.filter)
                }
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(10.dp),
                    modifier = if (!viewModel.prefs.lineWrap && viewModel.prefs.compact) Modifier
                        .horizontalScroll(
                            rememberScrollState()
                        )
                        .weight(1f) else Modifier
                        .weight(1f),
                ) {
                    itemsIndexed(
                        logs,
                        key = { _, log -> log.hashCode() }
                    ) { i, it ->
                        if (viewModel.prefs.compact)
                            Text(
                                text = it.annotated,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (i % 2 == 0)
                                        MaterialTheme.colorScheme.logLineAlt else MaterialTheme.typography.labelSmall.color
                                ),
                                softWrap = viewModel.prefs.lineWrap,
                                modifier = Modifier.padding(4.dp),
                            )
                        else LogEntry(it, selected = viewModel.selectedLogs)
                    }
                }
            }

        }
    }

    @Composable
    @OptIn(ExperimentalAnimationApi::class)
    private fun JumpFAB(
        viewModel: MainScreenViewModel,
        listState: LazyListState,
        lastIndex: Int
    ) {
        val scope = rememberCoroutineScope()
        AnimatedVisibility(
            visible = viewModel.freeScroll && viewModel.selectedLogs.isEmpty(),
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            SmallFloatingActionButton(
                onClick = {
                    viewModel.freeScroll = false
                    scope.launch {
                        if (lastIndex >= 0) listState.animateScrollToItem(lastIndex)
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Jump to bottom"
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Toolbar(
        viewModel: MainScreenViewModel
    ) {
        val navigator = LocalNavigator.current
        val ctx = LocalContext.current
        var menuOpened by remember {
            mutableStateOf(false)
        }

        TopAppBar(
            title = {
                RoundedTextBox(
                    text = viewModel.filter.text,
                    placeholder = stringResource(id = R.string.search)
                ) {
                    viewModel.filter.text = it
                }
            },
            actions = {
                Spacer(modifier = Modifier.width(10.dp))

//                Text(viewModel.logs.lastIndex.toString())

                // Open filter dialog
                IconButton(onClick = {
                    viewModel.filterOpened = !viewModel.filterOpened
                }) {
                    Icon(
                        painterResource(R.drawable.ic_filter_24),
                        contentDescription = stringResource(R.string.filter)
                    )
                }

                // Pause/Unpause logs
                IconButton(onClick = { viewModel.paused = !viewModel.paused }) {
                    if (viewModel.paused)
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Unpause logs")
                    else
                        Icon(
                            painterResource(R.drawable.ic_pause_24),
                            contentDescription = "Pause logs"
                        )
                }

                // Open dropdown menu
                IconButton(onClick = { menuOpened = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        stringResource(org.koin.android.R.string.abc_action_menu_overflow_description)
                    )
                }

                Box {
                    DropdownMenu(
                        expanded = menuOpened,
                        onDismissRequest = { menuOpened = false },
                        offset = DpOffset(
                            10.dp, 26.dp
                        )
                    ) {

                        // Clear logs
                        DropdownMenuItem(
                            text = { Text(text = "Clear") },
                            onClick = {
                                viewModel.logs.clear()
                                viewModel.selectedLogs.clear()
                                menuOpened = false
                            }
                        )

                        //Save logs to file
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.save)) },
                            onClick = {
                                ctx.saveText(
                                    viewModel.logs
                                        .sortedBy { it.createdAt }
                                        .joinToString("\n") {
                                            it.raw
                                        },
                                    "Logcat ${SimpleDateFormat("M/dd/yy H:mm:ss.SSS").format(Date())}"
                                )
                            }
                        )

                        // Go to crashes screen
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.crashes)) },
                            onClick = { navigator?.push(CrashesScreen()); menuOpened = false }
                        )

                        // Go to settings
                        DropdownMenuItem(
                            text = { Text(text = "Settings") },
                            onClick = { navigator?.push(SettingsScreen()); menuOpened = false }
                        )
                    }
                }
            }
        )
    }
}