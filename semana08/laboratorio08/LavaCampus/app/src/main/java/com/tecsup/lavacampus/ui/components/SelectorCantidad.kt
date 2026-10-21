package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.ui.theme.ColorEstudiante
import java.util.Locale

@Composable
fun SelectorCantidad(
    cantidad: Double,
    onCantidadCambiada: (Double) -> Unit,
    paso: Double = 1.0
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = { if (cantidad > 0) onCantidadCambiada(cantidad - paso) },
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFF1F5F9), CircleShape)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Restar", tint = Color.DarkGray)
        }

        Text(
            text = if (paso < 1.0) String.format(Locale.US, "%.1f", cantidad) else String.format(Locale.US, "%.0f", cantidad),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        IconButton(
            onClick = { onCantidadCambiada(cantidad + paso) },
            modifier = Modifier
                .size(32.dp)
                .background(ColorEstudiante, CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Sumar", tint = Color.White)
        }
    }
}