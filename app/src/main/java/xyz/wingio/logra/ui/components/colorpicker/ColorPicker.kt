package xyz.wingio.logra.ui.components.colorpicker

import android.graphics.Color.RGBToHSV
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import xyz.wingio.logra.utils.Utils.hexCode
import kotlin.math.sin

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ColorPicker(
    defaultColor: Color = Color.White,
    onColorChanged: (Color) -> Unit
) {
    val controller = rememberColorPickerController()
    var color by remember {
        mutableStateOf(defaultColor)
    }
    var colorText by remember {
        mutableStateOf(color.hexCode)
    }
    val size = with(LocalDensity.current) {
        150.dp.toPx()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HsvColorPicker(modifier = Modifier.size(150.dp), controller = controller) {
                if(it.fromUser) {
                    color = it.color
                    colorText = it.hexCode
                    onColorChanged(it.color)
                }
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }

        BrightnessSlider(modifier = Modifier
            .fillMaxWidth()
            .height(30.dp), controller = controller)

        OutlinedTextField(
            colorText,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                colorText = it
                runCatching {
                    val _color = Color(android.graphics.Color.parseColor("#$it"))
                    color = _color
                    controller.selectColor(_color, size)
                    onColorChanged(_color)
                }
            }
        )
    }
}


fun ColorPickerController.selectColor(color: Color, size: Float) {
    val hsv = FloatArray(3)
    RGBToHSV(
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt(),
        hsv
    )
    val radius = size / 2f
    val colorRadius: Float = hsv[1] * radius
    val angle: Double = ((1 - (hsv[0] / 360f)) * (2 * Math.PI))
    val midX: Float = size / 2f //midpoint of the circle
    val midY: Float = size / 2f
    val xOffset: Float =
        (kotlin.math.cos(angle) * colorRadius).toFloat() //offset from the midpoint of the circle
    val yOffset: Double = sin(angle) * colorRadius
    val x = midX + xOffset
    val y = midY + yOffset
    selectByCoordinate(x, y.toFloat(), fromUser = false)
}