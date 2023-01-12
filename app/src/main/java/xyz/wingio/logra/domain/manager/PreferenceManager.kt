package xyz.wingio.logra.domain.manager

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import xyz.wingio.logra.BuildConfig
import xyz.wingio.logra.R
import xyz.wingio.logra.domain.manager.base.BasePreferenceManager

class PreferenceManager(context: Context) :
    BasePreferenceManager(context.getSharedPreferences("prefs", Context.MODE_PRIVATE)) {

    var compact by booleanPreference("compact_mode", false)

    var lineWrap by booleanPreference("line_wrap", true)

    var monet by booleanPreference("monet", true)

    var timestampFormat by stringPreference("timestamp_format", "h:mm:ssa")

    var theme by enumPreference("theme", Theme.SYSTEM)

    var crashDetectorEnabled by booleanPreference("crash_detector_enabled", false)

    var dumpLogs by booleanPreference("dump_logs", false)

    // Dark mode log level colors
    var colorVD by colorPreference("color_vd", Color.DarkGray)
    var colorID by colorPreference("color_id", Color(0xFFaaaaaa))
    var colorDD by colorPreference("color_dd", Color(0xFF4D9250))
    var colorWD by colorPreference("color_wd", Color(0xFFD6C420))
    var colorED by colorPreference("color_ed", Color(0xFFD34339))
    var colorFD by colorPreference("color_fd", Color(0xFFA31111))

    // Light mode log level colors
    var colorVL by colorPreference("color_vl", Color(0xFFC5C5C5))
    var colorIL by colorPreference("color_il", Color(0xFF979797))
    var colorDL by colorPreference("color_dl", Color(0xFF2A972F))
    var colorWL by colorPreference("color_wl", Color(0xFFC0AD00))
    var colorEL by colorPreference("color_el", Color(0xFFA00A00))
    var colorFL by colorPreference("color_fl", Color(0xFF7A0909))

    var currentIcon by enumPreference("current_icon", Icon.DEFAULT)
    var easterEggDiscovered by booleanPreference("easter_egg_discovered", false)

}

enum class Theme {
    SYSTEM,
    LIGHT,
    DARK
}

@Stable
enum class Icon(
    val className: String,
    @DrawableRes val drawable: Int,
    @StringRes val nameRes: Int,
    @StringRes val description: Int
) {
    DEFAULT(
        BuildConfig.APPLICATION_ID + ".MainActivity",
        R.drawable.ic_launcher,
        R.string.icon_default_name,
        R.string.icon_default_description
    ),
    LEGACY(
        BuildConfig.APPLICATION_ID + ".icons.Legacy",
        R.drawable.ic_launcher_legacy,
        R.string.icon_legacy_name,
        R.string.icon_legacy_description
    ),
    NORD(
        BuildConfig.APPLICATION_ID + ".icons.Nord",
        R.drawable.ic_launcher_nord,
        R.string.icon_nord_name,
        R.string.icon_nord_description
    ),
    MOCHA(
        BuildConfig.APPLICATION_ID + ".icons.Mocha",
        R.drawable.ic_launcher_mocha,
        R.string.icon_mocha_name,
        R.string.icon_mocha_description
    ),
    NEON(
        BuildConfig.APPLICATION_ID + ".icons.Neon",
        R.drawable.ic_launcher_neon,
        R.string.icon_neon_name,
        R.string.icon_neon_description
    ),
    PRIDE(
        BuildConfig.APPLICATION_ID + ".icons.Pride",
        R.drawable.ic_launcher_pride,
        R.string.icon_pride_name,
        R.string.icon_pride_description
    )
}