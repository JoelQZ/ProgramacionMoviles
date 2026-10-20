package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tecsup.lavacampus.ui.theme.ColorEstudiante

@Composable
fun IconoServicio(
    nombreServicio: String,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    val icono: ImageVector = when {
        nombreServicio.contains("Planchado", ignoreCase = true) -> Icons.Default.Checkroom
        nombreServicio.contains("Edredón", ignoreCase = true) ||
                nombreServicio.contains("Camisa", ignoreCase = true) ||
                nombreServicio.contains("Seco", ignoreCase = true) -> Icons.Default.DryCleaning
        else -> Icons.Default.LocalLaundryService
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE0F2FE), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = nombreServicio,
            tint = ColorEstudiante,
            modifier = Modifier.size(size)
        )
    }
}