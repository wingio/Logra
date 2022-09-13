package xyz.wingio.logra.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import xyz.wingio.logra.R
import xyz.wingio.logra.ui.components.filter.FilterRow
import xyz.wingio.logra.ui.components.filter.RoundedTextBox
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
        viewModel: MainScreenViewModel = getViewModel()
    ) {
        val listState = rememberLazyListState()
        val logs = runCatching { viewModel.filterLogs() }.getOrElse { viewModel.logs }

        LaunchedEffect(Unit) {
            listState.interactionSource.interactions.collectLatest {
                if (it is DragInteraction.Start) viewModel.freeScroll.value = true
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { Toolbar(viewModel) },
            floatingActionButton = { JumpFAB(viewModel, listState) },
            bottomBar = {
                SelectionPopup(viewModel.selectedLogs) {
                    viewModel.selectedLogs.clear()
                }
            }
        ) { pad ->

            LaunchedEffect(logs.size) {
                if (logs.isNotEmpty() && !viewModel.freeScroll.value) {
                    listState.animateScrollToItem(logs.lastIndex)
                }
            }

            Column(
                Modifier
                    .padding(pad)
            ) {
                AnimatedVisibility(visible = viewModel.filterOpened.value) {
                    FilterRow(oldFilter = viewModel.filter.value)
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
                        key = { i, log -> "$i${log.hashCode()}" }
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
        listState: LazyListState
    ) {
        val scope = rememberCoroutineScope()
        AnimatedVisibility(
            visible = viewModel.freeScroll.value && viewModel.selectedLogs.isEmpty(),
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            SmallFloatingActionButton(
                onClick = {
                    viewModel.freeScroll.value = false
                    scope.launch {
                        if (viewModel.logs.isNotEmpty()) listState.animateScrollToItem(viewModel.logs.lastIndex)
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

        var searchText by remember {
            mutableStateOf(viewModel.filter.value.text)
        }

        SmallTopAppBar(
            title = {
                RoundedTextBox (
                    text = searchText,
                    placeholder = stringResource(id = R.string.search)
                ) {
                    searchText = it
                    viewModel.filter.value.text = it
                }
            },
            actions = {
                Spacer(modifier = Modifier.width(10.dp))
                // Open filter dialog
                IconButton(onClick = {
                    viewModel.filterOpened.value = !viewModel.filterOpened.value
                }) {
                    Icon(
                        painterResource(R.drawable.ic_filter_24),
                        contentDescription = stringResource(R.string.filter)
                    )
                }

                // Pause/Unpause logs
                IconButton(onClick = { viewModel.paused.value = !viewModel.paused.value }) {
                    if (viewModel.paused.value)
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

                DropdownMenu(
                    expanded = menuOpened,
                    onDismissRequest = { menuOpened = false },
                    offset = DpOffset(
                        (-10).dp, 0.dp
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

                    // Go to settings
                    DropdownMenuItem(
                        text = { Text(text = "Settings") },
                        onClick = { navigator?.push(SettingsScreen()); menuOpened = false }
                    )
                }
            }
        )
    }
}