package com.quijada.tecsupfit

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

enum class DestinoTab(val titulo: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Default.Home),
    RESERVAS("Reservas", Icons.Default.DateRange),
    RUTINAS("Rutinas", Icons.Default.List),
    PERFIL("Perfil", Icons.Default.Person)
}

@Composable
fun AppNavigation() {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var claseSeleccionada by remember { mutableStateOf<ClaseGimnasio?>(null) }
    var pantallaActual by remember { mutableStateOf("principal") }

    val mostrarBottomBar = pantallaActual == "principal"

    Scaffold(
        bottomBar = {
            if (mostrarBottomBar) {
                NavigationBar {
                    DestinoTab.entries.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = tabSeleccionada == index,
                            onClick = { tabSeleccionada = index },
                            icon = { Icon(tab.icono, contentDescription = tab.titulo) },
                            label = { Text(tab.titulo) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Modifier.padding(paddingValues)

        when (pantallaActual) {
            "principal" -> {
                when (tabSeleccionada) {
                    0 -> HomeScreen(
                        onClaseClick = { clase ->
                            claseSeleccionada = clase
                            pantallaActual = "detalle"
                        }
                    )
                    1 -> ReservasScreen()
                    2 -> Text("Pantalla Rutinas (En desarrollo)")
                    3 -> ProfileScreen()
                }
            }
            "detalle" -> {
                claseSeleccionada?.let { clase ->
                    DetailScreen(
                        clase = clase,
                        onVolverClick = { pantallaActual = "principal" },
                        onReservarClick = { pantallaActual = "confirmacion" }
                    )
                }
            }
            "confirmacion" -> {
                claseSeleccionada?.let { clase ->
                    ConfirmationScreen(
                        clase = clase,
                        onVerReservasClick = {
                            pantallaActual = "principal"
                            tabSeleccionada = 1
                        }
                    )
                }
            }
        }
    }
}