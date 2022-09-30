package xyz.wingio.logra.ui.components.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import xyz.wingio.logra.R
import xyz.wingio.logra.domain.logcat.LogLevel
import xyz.wingio.logra.domain.logcat.filter.Filter
import xyz.wingio.logra.ui.components.settings.SettingsChoiceDialog
import xyz.wingio.logra.utils.Utils.capitalizedName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterRow(
    oldFilter: Filter
) {
    val scrollState = rememberScrollState()

    val filter by remember(oldFilter) {
        derivedStateOf {
            oldFilter
        }
    }

    var newFilterDDOpened by remember {
        mutableStateOf(false)
    }

    val showDivider = filter.levels.isNotEmpty()

    Row(
        modifier = Modifier
            .padding(10.dp)
            .horizontalScroll(scrollState)
    ) {

        Row(
            modifier = Modifier.padding(end = 10.dp)
        ) {
            IconButton(
                onClick = {
                    val defaultFilter = Filter()
                    filter.apply {
                        levels = defaultFilter.levels
                        tags = defaultFilter.tags
                    }
                }
            ) { Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Reset") }
            IconButton(
                onClick = { newFilterDDOpened = true }
            ) { Icon(imageVector = Icons.Filled.Add, contentDescription = "New Filter") }
        }

        if (showDivider) Box(
            modifier = Modifier
                .width(1.dp)
                .height(30.dp)
                .background(MaterialTheme.colorScheme.primary.copy(0.4f))
                .align(Alignment.CenterVertically)
        )

        Row(
            modifier = Modifier.padding(start = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            filter.tags.forEach {
                ElevatedAssistChip(
                    onClick = {
                        if (filter.tags.contains(it)) filter.tags.remove(it)
                    },
                    label = { Text(text = it) },
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_tag_24),
                            stringResource(id = R.string.tag),
                            modifier = Modifier.size(15.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_clear_24),
                            stringResource(id = R.string.remove),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                )
            }

            filter.levels.forEach {
                ElevatedAssistChip(
                    onClick = {
                        if (filter.levels.contains(it)) filter.levels.remove(it)
                    },
                    label = { Text(text = it.capitalizedName) },
                    trailingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_clear_24),
                            stringResource(id = R.string.remove),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                )
            }
        }

        NewFilterDropdown(expanded = newFilterDDOpened, filter) {
            newFilterDDOpened = false
        }

    }

}

@Composable
private fun NewFilterDropdown(
    expanded: Boolean,
    filter: Filter,
    onDismissRequest: () -> Unit
) {
    var levelOpened by remember {
        mutableStateOf(false)
    }

    var tagOpened by remember {
        mutableStateOf(false)
    }

    DropdownMenu(expanded, onDismissRequest, offset = DpOffset(35.dp, 0.dp)) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.level)) },
            onClick = { onDismissRequest(); levelOpened = true },
            enabled = filter.levels.size != LogLevel.values().size
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.tag)) },
            onClick = { onDismissRequest(); tagOpened = true },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.before)) },
            onClick = { onDismissRequest() },
            enabled = false
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.after)) },
            onClick = { onDismissRequest() },
            enabled = false
        )
    }

    SettingsChoiceDialog(
        title = { Text(stringResource(R.string.level)) },
        default = LogLevel.VERBOSE,
        visible = levelOpened,
        onRequestClose = {
            levelOpened = false
            if (!filter.levels.contains(it)) filter.levels.add(it)
        }
    )

    TagInputDialog(
        onDismissRequest = { tagOpened = false },
        onConfirm = { if (!filter.tags.contains(it)) filter.tags.add(it) },
        visible = tagOpened
    )

}