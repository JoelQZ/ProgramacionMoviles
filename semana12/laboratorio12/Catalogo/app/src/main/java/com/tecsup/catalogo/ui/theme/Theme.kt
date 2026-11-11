package com.tecsup.catalogo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberpunkDarkColorScheme = darkColorScheme(
    primary = NeonPrimary,
    onPrimary = NeonOnPrimary,
    primaryContainer = Color(0xFF2A1B4E),
    onPrimaryContainer = Color(0xFFE2D9F3),
    secondary = NeonSecondary,
    onSecondary = Color(0xFF003730),
    secondaryContainer = Color(0xFF0A3641),
    onSecondaryContainer = Color(0xFF99F6E4),
    tertiary = NeonTertiary,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF5A0033),
    onTertiaryContainer = Color(0xFFFFD8E4),
    background = NeonBackground,
    onBackground = NeonOnSurface,
    surface = NeonSurface,
    onSurface = NeonOnSurface,
    surfaceVariant = NeonSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    error = NeonError,
    onError = Color(0xFFFFFFFF)
)

@Composable
fun CatalogoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberpunkDarkColorScheme,
        typography = Typography,
        content = content
    )
}
