package com.tecsup.murocampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TechBluePrimary,
    onPrimary = Color.White,
    secondary = SkyBlueSecondary,
    background = TextCharcoal,
    surface = Color(0xFF1E293B),
    onBackground = BackgroundIce,
    onSurface = BackgroundIce
)

private val LightColorScheme = lightColorScheme(
    primary = TechBluePrimary,
    onPrimary = Color.White,
    secondary = SkyBlueSecondary,
    secondaryContainer = SkyBlueContainer,
    onSecondaryContainer = TechBlueDark,
    background = BackgroundIce,
    onBackground = TextCharcoal,
    surface = SurfaceWhite,
    onSurface = TextCharcoal,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSlate,
    outline = BorderLight
)

@Composable
fun MuroCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
