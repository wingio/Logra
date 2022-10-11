package xyz.wingio.logra.utils

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

fun Navigator.navigate(screen: Screen) {
    runCatching {
        push(screen)
    }
}