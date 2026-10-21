package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.navigation.Rutas
import com.tecsup.lavacampus.ui.theme.ColorAdministrador
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosScreen(
    viewModel: ServiciosViewModel,
    onNuevoServicio: () -> Unit,
    onEditarServicio: (Servicio) -> Unit = {},
    onNavegarTab: (String) -> Unit = {}
) {
    val servicios by viewModel.todosLosServicios.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Servicios y tarifas",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoServicio,
                containerColor = ColorAdministrador,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo servicio")
            }
        },
        bottomBar = {
            BarraInferiorAdmin(rutaSeleccionada = Rutas.SERVICIOS_ADMIN, onNavegar = onNavegarTab)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(servicios) { s ->
                TarjetaServicioAdmin(
                    servicio = s,
                    onToggleActivo = { activo ->
                        viewModel.cambiarEstadoActivo(s, activo)
                    },
                    onEditar = { onEditarServicio(s) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaServicioAdmin(
    servicio: Servicio,
    onToggleActivo: (Boolean) -> Unit,
    onEditar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    IconoServicio(
                        nombreServicio = servicio.nombre,
                        backgroundColor = ColorAdministrador.copy(alpha = 0.15f),
                        tintColor = ColorAdministrador
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        servicio.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = String.format(Locale.US, "S/ %.2f por %s", servicio.precio, servicio.unidad),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = servicio.activo,
                    onCheckedChange = onToggleActivo,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = ColorAdministrador,
                        checkedThumbColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onEditar) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar servicio",
                        tint = ColorAdministrador,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BarraInferiorAdmin(rutaSeleccionada: String, onNavegar: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.PANEL_ADMIN,
            onClick = { onNavegar(Rutas.PANEL_ADMIN) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Panel", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorAdministrador,
                selectedTextColor = ColorAdministrador,
                indicatorColor = ColorAdministrador.copy(alpha = 0.15f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.BANDEJA_PEDIDOS,
            onClick = { onNavegar(Rutas.BANDEJA_PEDIDOS) },
            icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
            label = { Text("Pedidos", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorAdministrador,
                selectedTextColor = ColorAdministrador,
                indicatorColor = ColorAdministrador.copy(alpha = 0.15f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.SERVICIOS_ADMIN,
            onClick = { onNavegar(Rutas.SERVICIOS_ADMIN) },
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = null) },
            label = { Text("Servicios", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorAdministrador,
                selectedTextColor = ColorAdministrador,
                indicatorColor = ColorAdministrador.copy(alpha = 0.15f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.CLIENTES_ADMIN,
            onClick = { onNavegar(Rutas.CLIENTES_ADMIN) },
            icon = { Icon(Icons.Default.People, contentDescription = null) },
            label = { Text("Clientes", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorAdministrador,
                selectedTextColor = ColorAdministrador,
                indicatorColor = ColorAdministrador.copy(alpha = 0.15f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.AJUSTES_ADMIN,
            onClick = { onNavegar(Rutas.AJUSTES_ADMIN) },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Ajustes", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorAdministrador,
                selectedTextColor = ColorAdministrador,
                indicatorColor = ColorAdministrador.copy(alpha = 0.15f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}