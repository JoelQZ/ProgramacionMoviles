package com.quijada.clinicasalud.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleSecondary,
    onPrimaryContainer = PurplePrimary,
    secondary = PurpleSecondary,
    onSecondary = PurplePrimary,
    background = PurpleBackground,
    surface = SurfaceCard,
    onSurface = Color(0xFF1D1B20)
)

@Composable
fun ClinicaSaludTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}