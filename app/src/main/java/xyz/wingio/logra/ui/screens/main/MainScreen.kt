package xyz.wingio.logra.ui.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import xyz.wingio.logra.R
import xyz.wingio.logra.ui.viewmodels.main.MainScreenViewModel
import xyz.wingio.logra.ui.widgets.logs.LogEntry
import xyz.wingio.logra.utils.Logger

class MainScreen : Screen {

    //TODO: Make this a toggleable setting
    private val compact = false

    @Composable
    override fun Content() = Screen()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Screen(
        viewModel: MainScreenViewModel = getViewModel()
    ) {
        val listState = rememberLazyListState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { Toolbar(viewModel, listState) }
        ) { pad ->

            LaunchedEffect(viewModel.logs.size) {
                if(viewModel.logs.lastIndex >= 0 && !viewModel.freeScroll.value) {
                    listState.animateScrollToItem(viewModel.logs.lastIndex)
                }
            }

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier
                    .padding(pad),
                userScrollEnabled = viewModel.freeScroll.value
            ) {
                items(
                    viewModel.logs
                ) {
                    if (compact)
                        Text(
                            it.raw,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = if (viewModel.logs.indexOf(it) % 2 == 0) Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                                )
                                .padding(4.dp) else Modifier.padding(4.dp)
                        )
                    else LogEntry(it)
                }
            }
        }
    }

    @Composable
    private fun Toolbar(
        viewModel: MainScreenViewModel,
        listState: LazyListState
    ) {
        val scope = rememberCoroutineScope()
        SmallTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.app_name))
            },
            actions = {
                IconButton(onClick = {  }) {
                    Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
                }
                IconButton(onClick = { viewModel.paused.value = !viewModel.paused.value }) {
                    if(viewModel.paused.value)
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Unpause logs")
                    else
                        Icon(painterResource(R.drawable.ic_pause_24), contentDescription = "Pause logs")
                }
                IconButton(onClick = { viewModel.logs.clear() }) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Clear")
                }
                IconButton(onClick = {
                    viewModel.freeScroll.value = !viewModel.freeScroll.value
                    if(!viewModel.freeScroll.value) {
                        scope.launch {
                            listState.animateScrollToItem(viewModel.logs.lastIndex)
                        }
                    }
                }) {
                    Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Toggle free scrolling")
                }
            }
        )
    }
}