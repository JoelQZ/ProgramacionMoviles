package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.ClienteResumen
import com.tecsup.lavacampus.ui.navigation.Rutas
import com.tecsup.lavacampus.ui.theme.ColorAdministrador
import com.tecsup.lavacampus.viewmodel.ClientesViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(
    viewModel: ClientesViewModel,
    onVerHistorialCliente: (Int) -> Unit = {},
    onNavegarTab: (String) -> Unit = {}
) {
    val clientes by viewModel.clientes.collectAsState()
    val query by viewModel.textoBusqueda.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Clientes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(end = 48.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavegarTab(Rutas.PANEL_ADMIN) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            BarraInferiorAdmin(rutaSeleccionada = Rutas.CLIENTES_ADMIN, onNavegar = onNavegarTab)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.buscar(it) },
                placeholder = { Text("Buscar estudiante...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = ColorAdministrador,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(clientes) { c ->
                    TarjetaCliente(
                        cliente = c,
                        onClick = { onVerHistorialCliente(c.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaCliente(
    cliente: ClienteResumen,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = ColorAdministrador.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = cliente.nombre.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ColorAdministrador
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = cliente.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Ver pedidos del cliente",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = cliente.codigo,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${cliente.totalPedidos} pedidos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = ColorAdministrador
                    )

                    Text(
                        text = String.format(Locale.US, "S/ %.2f", cliente.totalMonto),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
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
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Panel") },
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
            icon = { Icon(Icons.Default.ListAlt, contentDescription = "Pedidos") },
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
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = "Servicios") },
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
            icon = { Icon(Icons.Default.People, contentDescription = "Clientes") },
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
            icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
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