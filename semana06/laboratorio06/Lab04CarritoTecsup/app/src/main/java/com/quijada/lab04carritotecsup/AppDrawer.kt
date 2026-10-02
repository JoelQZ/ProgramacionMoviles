package com.quijada.lab04carritotecsup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    destinoSeleccionado: String,
    cantidadFavoritos: Int = 0,
    onDestinoSeleccionado: (String) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Menú Principal",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}