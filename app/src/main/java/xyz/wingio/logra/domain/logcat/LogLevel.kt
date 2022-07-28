package xyz.wingio.logra.domain.logcat

import androidx.compose.ui.graphics.Color

enum class LogLevel(val color: Color) {
    VERBOSE(Color.DarkGray),
    DEBUG(Color(0xFF4D9250)),
    INFO(Color(0xFFaaaaaa)),
    WARNING(Color(0xFFD6C420)),
    ERROR(Color(0xFFD34339)),
    FATAL(Color(0xFFA31111))
}