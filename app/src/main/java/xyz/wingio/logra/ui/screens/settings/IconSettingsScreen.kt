package xyz.wingio.logra.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import xyz.wingio.logra.R
import xyz.wingio.logra.domain.manager.Icon
import xyz.wingio.logra.ui.viewmodels.settings.icon.IconSettingsViewModel
import xyz.wingio.logra.utils.getBitmap
import androidx.compose.material3.Icon as M3Icon

class IconSettingsScreen : Screen {

    @Composable
    override fun Content() = Screen()

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun Screen(
        viewModel: IconSettingsViewModel = getScreenModel()
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val icons = remember {
            Icon.values().toList()
        }

        Scaffold(
            topBar = { Toolbar(scrollBehavior) },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                items(icons) { icon ->
                    if (icon == Icon.LEGACY) {
                        if (viewModel.settings.easterEggDiscovered) {
                            IconChoice(
                                icon = icon,
                                onClick = { viewModel.changeIcon(icon) }
                            )
                        }
                    } else {
                        IconChoice(
                            icon = icon,
                            onClick = { viewModel.changeIcon(icon) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun Toolbar(
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        val navigator = LocalNavigator.current

        LargeTopAppBar(
            title = { Text(stringResource(R.string.settings_app_icon)) },
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                IconButton(onClick = { navigator?.pop() }) {
                    M3Icon(Icons.Filled.ArrowBack, stringResource(R.string.back))
                }
            }
        )
    }

    @Composable
    private fun IconChoice(
        icon: Icon,
        viewModel: IconSettingsViewModel = getScreenModel(),
        onClick: () -> Unit
    ) {
        val bitmap = LocalContext.current.getBitmap(icon.drawable, 45).asImageBitmap()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(45.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    stringResource(icon.nameRes),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    stringResource(icon.description),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (viewModel.settings.currentIcon == icon) {
                M3Icon(
                    imageVector = Icons.Filled.CheckCircle, contentDescription = stringResource(
                        id = R.string.currently_used
                    )
                )
            }
        }
    }
}