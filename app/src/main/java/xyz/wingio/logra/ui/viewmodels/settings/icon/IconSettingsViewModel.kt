package xyz.wingio.logra.ui.viewmodels.settings.icon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import cafe.adriel.voyager.core.model.ScreenModel
import xyz.wingio.logra.domain.manager.Icon
import xyz.wingio.logra.domain.manager.PreferenceManager

class IconSettingsViewModel(
    private val context: Context,
    val settings: PreferenceManager
) : ScreenModel {

    fun changeIcon(icon: Icon) {
        val manager = context.packageManager
        val oldIcon = settings.currentIcon

        manager.setComponentEnabledSetting(
            ComponentName(
                context,
                oldIcon.className
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )

        manager.setComponentEnabledSetting(
            ComponentName(
                context,
                icon.className
            ),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        settings.currentIcon = icon
    }

}