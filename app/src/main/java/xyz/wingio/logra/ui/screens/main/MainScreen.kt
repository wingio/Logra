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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import xyz.wingio.logra.R
import xyz.wingio.logra.ui.screens.settings.SettingsScreen
import xyz.wingio.logra.ui.theme.logLineAlt
import xyz.wingio.logra.ui.viewmodels.main.MainScreenViewModel
import xyz.wingio.logra.ui.widgets.logs.LogEntry

class MainScreen : Screen {

    @Composable
    override fun Content() = Screen()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Screen(
        viewModel: MainScreenViewModel = getViewModel()
    ) {
        val listState = rememberLazyListState()

        LaunchedEffect(Unit) {
            listState.interactionSource.interactions.collectLatest {
                if (it is DragInteraction.Start) viewModel.freeScroll.value = true
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { Toolbar(viewModel) },
            floatingActionButton = { JumpFAB(viewModel, listState) }
        ) { pad ->

            LaunchedEffect(viewModel.logs.size) {
                if (viewModel.logs.size > 0 && !viewModel.freeScroll.value) {
                    listState.animateScrollToItem(viewModel.logs.lastIndex)
                }
            }

            Column(
                Modifier
                    .padding(pad)
            ) {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(10.dp),
                    modifier = if (!viewModel.prefs.lineWrap && viewModel.prefs.compact) Modifier
                        .horizontalScroll(
                            rememberScrollState()
                        )
                        .weight(1f) else Modifier
                        .weight(1f),
                ) {
                    itemsIndexed(
                        viewModel.logs
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
                        else LogEntry(it)
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
            visible = viewModel.freeScroll.value,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            SmallFloatingActionButton(
                onClick = {
                    viewModel.freeScroll.value = false
                    scope.launch {
                        if (viewModel.logs.size > 0) listState.animateScrollToItem(viewModel.logs.lastIndex)
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

    @Composable
    private fun Toolbar(
        viewModel: MainScreenViewModel
    ) {
        val navigator = LocalNavigator.current
        var menuOpened by remember {
            mutableStateOf(false)
        }
        SmallTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.app_name))
            },
            actions = {

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
                    Icon(imageVector = Icons.Filled.MoreVert, stringResource(org.koin.android.R.string.abc_action_menu_overflow_description))
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
                        onClick = { viewModel.logs.clear(); menuOpened = false }
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