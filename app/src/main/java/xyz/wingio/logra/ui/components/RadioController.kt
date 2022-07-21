package xyz.wingio.logra.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import xyz.wingio.logra.utils.Utils.capitalizedName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun <reified E : Enum<E>> EnumRadioController(
    default: E,
    crossinline onChoiceSelected: (E) -> Unit
) {
    var choice by remember { mutableStateOf(default) }

    Column {
        enumValues<E>().forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        choice = it
                        onChoiceSelected(it)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(it.capitalizedName)
                Spacer(Modifier.weight(1f))
                RadioButton(
                    selected = it == choice,
                    onClick = {
                        choice = it
                        onChoiceSelected(it)
                    })
            }
        }
    }

}