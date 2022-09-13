package xyz.wingio.logra.ui.components.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import xyz.wingio.logra.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedTextBox (
    text: String = "",
    placeholder: String = "",
    onTextChanged: ((String) -> Unit)
) {
    val colors = TextFieldDefaults.textFieldColors()
    BasicTextField(
        value = text,
        onValueChange = onTextChanged,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = colors.textColor(
                enabled = true
            ).value
        ),
        cursorBrush = SolidColor(colors.cursorColor(false).value),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(200.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)),
        decorationBox = { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = text,
                innerTextField = innerTextField,
                placeholder = { Text(placeholder) },
                trailingIcon = {
                    if (text.isNotEmpty()) IconButton(onClick = {
                        onTextChanged("")
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_clear_24),
                            contentDescription = stringResource(id = R.string.clear)
                        )
                    }
                },
                singleLine = true,
                enabled = true,
                interactionSource = remember { MutableInteractionSource() },
                visualTransformation = VisualTransformation.None,
                contentPadding = PaddingValues(
                    start = 14.dp,
                    end = 12.dp,
                    top = 10.dp,
                    bottom = 10.dp
                )
            )
        }
    )
}