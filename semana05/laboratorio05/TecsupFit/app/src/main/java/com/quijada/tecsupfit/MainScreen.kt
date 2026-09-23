package com.quijada.tecsupfit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

enum class PantallaTab(val titulo: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Default.Home),
    RESERVAS("Reservas", Icons.Default.DateRange),
    RUTINAS("Rutinas", Icons.Default.List),
    PERFIL("Perfil", Icons.Default.Person)
}

@Composable
fun MainScreen() {
    var tabSeleccionada by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                PantallaTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = tabSeleccionada == index,
                        onClick = { tabSeleccionada = index },
                        icon = { Icon(tab.icono, contentDescription = tab.titulo) },
                        label = { Text(tab.titulo) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (tabSeleccionada) {
                0 -> Text("Pantalla Inicio")
                1 -> Text("Pantalla Reservas")
                2 -> Text("Pantalla Rutinas")
                3 -> Text("Pantalla Perfil")
            }
        }
    }
}