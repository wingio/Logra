package xyz.wingio.logra.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap

private val cachedBitmaps: MutableMap<Int, MutableMap<Int, Bitmap>> = mutableMapOf()

context(Context)
val Int.dp: Int
    get() = (45 * this@Context.resources.displayMetrics.density + 0.5f).toInt()

fun Context.getBitmap(@DrawableRes icon: Int, size: Int): Bitmap {
    cachedBitmaps[icon]?.let { it[size]?.let { bitmap -> return bitmap } }
    val sizePx = size.dp

    val bitmap = getDrawable(icon)!!.toBitmap(
        height = sizePx,
        width = sizePx
    )

    cachedBitmaps[icon] = mutableMapOf()
    cachedBitmaps[icon]?.let {
        it[size] = bitmap
    }

    return bitmap
}