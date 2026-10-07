package com.saludplus.citas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    secondary = AzulSecundario,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro
)

@Composable
fun SaludPlusCitasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}