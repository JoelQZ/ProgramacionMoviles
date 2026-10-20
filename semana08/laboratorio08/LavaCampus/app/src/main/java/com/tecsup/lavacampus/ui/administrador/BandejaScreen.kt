package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.EstadoPedidoChip
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.navigation.Rutas
import com.tecsup.lavacampus.ui.theme.ColorAdministrador
import com.tecsup.lavacampus.viewmodel.BandejaViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandejaScreen(
    viewModel: BandejaViewModel,
    onVerGestionPedido: (Int) -> Unit,
    onNavegarTab: (String) -> Unit = {}
) {
    val pedidos by viewModel.pedidos.collectAsState()
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val filtroEstado by viewModel.filtroEstado.collectAsState()

    val opcionesFiltro = listOf("Todos", "Recibido", "En proceso", "Listo")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Pedidos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BarraInferiorAdmin(rutaSeleccionada = Rutas.BANDEJA_PEDIDOS, onNavegar = onNavegarTab)
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { viewModel.actualizarBusqueda(it) },
                placeholder = { Text("Buscar por estudiante o número", fontSize = 13.sp, color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFFE2E8F0),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(opcionesFiltro) { item ->
                    FilterChip(
                        selected = filtroEstado == item,
                        onClick = { viewModel.seleccionarFiltro(item) },
                        label = { Text(item, fontSize = 12.sp) },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ColorAdministrador,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color.Gray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (pedidos.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No hay pedidos registrados", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pedidos) { item ->
                        ItemPedidoAdminTarjeta(
                            item = item,
                            onClick = { onVerGestionPedido(item.pedido.id) },
                            onAccionRapida = {
                                viewModel.avanzarEstadoRapido(item)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemPedidoAdminTarjeta(
    item: PedidoConDetalle,
    onClick: () -> Unit,
    onAccionRapida: () -> Unit
) {
    val p = item.pedido
    val estStr = p.estado.uppercase()

    val estadoEnum = try {
        EstadoPedido.valueOf(estStr)
    } catch (e: Exception) {
        EstadoPedido.RECIBIDO
    }

    val primerServicio = item.detalles.firstOrNull()?.servicio?.nombre ?: ""
    val nombreEstudiante = item.usuario?.nombre ?: "Estudiante"

    val resumenDetalle = item.detalles.joinToString(" + ") { d ->
        val unStr = if (d.servicio.unidad.equals("KG", ignoreCase = true)) "kg" else "p"
        "${d.detalle.cantidad} $unStr"
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                        IconoServicio(nombreServicio = primerServicio)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Pedido #${String.format(Locale.US, "%03d", p.id)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("$nombreEstudiante • $resumenDetalle", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Text(
                    String.format(Locale.US, "S/ %.2f", p.total),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ColorAdministrador
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EstadoPedidoChip(estado = estadoEnum)

                when (estStr) {
                    "RECIBIDO" -> {
                        Button(
                            onClick = onAccionRapida,
                            colors = ButtonDefaults.buttonColors(containerColor = ColorAdministrador),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Iniciar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    "EN_PROCESO" -> {
                        Button(
                            onClick = onClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ColorAdministrador),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Marcar listo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    "LISTO" -> {
                        Button(
                            onClick = onClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ColorAdministrador),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Entregar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BarraInferiorAdmin(rutaSeleccionada: String, onNavegar: (String) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.PANEL_ADMIN,
            onClick = { onNavegar(Rutas.PANEL_ADMIN) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Panel", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.BANDEJA_PEDIDOS,
            onClick = { onNavegar(Rutas.BANDEJA_PEDIDOS) },
            icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
            label = { Text("Pedidos", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ColorAdministrador, indicatorColor = Color(0xFFF3EDF7))
        )
        NavigationBarItem(
            selected = false, onClick = {},
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = null) },
            label = { Text("Servicios", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = false, onClick = {},
            icon = { Icon(Icons.Default.People, contentDescription = null) },
            label = { Text("Clientes", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = false, onClick = {},
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Ajustes", fontSize = 10.sp) }
        )
    }
}