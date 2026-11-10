package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = StreamCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = StreamViolet,
    onSecondary = Color(0xFF381E72),
    secondaryContainer = Color(0xFF4F378B),
    onSecondaryContainer = Color(0xFFEADDFF),
    tertiary = StreamLiveRed,
    onTertiary = Color.White,
    background = StreamBgDark,
    onBackground = StreamTextPrimary,
    surface = StreamSurfaceDark,
    onSurface = StreamTextPrimary,
    surfaceVariant = StreamSurfaceCard,
    onSurfaceVariant = StreamTextSecondary,
    outline = Color(0xFF2E384D),
    outlineVariant = Color(0xFF1E2536),
    error = StreamLiveRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
