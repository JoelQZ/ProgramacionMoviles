package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun obtenerBrushFondoLavacampus(): Brush {
    val esOscuro = isSystemInDarkTheme()
    return if (esOscuro) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F172A),
                Color(0xFF1E293B),
                Color(0xFF0288D1)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF00B4DB),
                Color(0xFF0288D1),
                Color(0xFF0083B0)
            )
        )
    }
}