package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
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
import com.tecsup.lavacampus.ui.theme.ColorEstudiante
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPedidosScreen(
    viewModel: MisPedidosViewModel,
    onNuevoPedido: () -> Unit,
    onVerDetallePedido: (Long) -> Unit,
    onVolver: () -> Unit = {},
    onNavegarTab: (String) -> Unit = {}
) {
    var pestanaSeleccionada by remember { mutableIntStateOf(0) }
    val activos by viewModel.activos.collectAsState()
    val historial by viewModel.historial.collectAsState()

    var pedidoACancelar by remember { mutableStateOf<PedidoConDetalle?>(null) }

    val listaMostrar = if (pestanaSeleccionada == 0) activos else historial
    val pedidoListo = activos.firstOrNull { it.pedido.estado.uppercase() == "LISTO" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Mis pedidos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(end = 48.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BarraInferiorEstudiante(
                rutaSeleccionada = Rutas.MIS_PEDIDOS,
                onNavegar = onNavegarTab
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoPedido,
                containerColor = ColorEstudiante,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo pedido")
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { pestanaSeleccionada = 0 },
                        shape = RoundedCornerShape(18.dp),
                        color = if (pestanaSeleccionada == 0) ColorEstudiante else Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "Activos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (pestanaSeleccionada == 0) Color.White else Color.Gray
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { pestanaSeleccionada = 1 },
                        shape = RoundedCornerShape(18.dp),
                        color = if (pestanaSeleccionada == 1) ColorEstudiante else Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "Historial",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (pestanaSeleccionada == 1) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (listaMostrar.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (pestanaSeleccionada == 0) "No tienes pedidos activos" else "No hay historial de pedidos",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaMostrar) { item ->
                        ItemPedidoTarjeta(
                            item = item,
                            onClick = { onVerDetallePedido(item.pedido.id.toLong()) },
                            onCancelar = { pedidoACancelar = item }
                        )
                    }

                    if (pestanaSeleccionada == 0 && pedidoListo != null) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Aviso", fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), fontSize = 13.sp)
                                    Text(
                                        "Tu pedido #${String.format(Locale.US, "%03d", pedidoListo.pedido.id)} está listo; recógelo en el casillero indicado.",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (pedidoACancelar != null) {
        AlertDialog(
            onDismissRequest = { pedidoACancelar = null },
            title = { Text("¿Cancelar pedido?", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de cancelar el pedido #${String.format(Locale.US, "%03d", pedidoACancelar!!.pedido.id)}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val p = pedidoACancelar!!
                        viewModel.cancelarPedido(p.pedido.id, p.pedido.casillero)
                        pedidoACancelar = null
                    }
                ) {
                    Text("Sí, cancelar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pedidoACancelar = null }) {
                    Text("Volver")
                }
            }
        )
    }
}

@Composable
private fun ItemPedidoTarjeta(
    item: PedidoConDetalle,
    onClick: () -> Unit,
    onCancelar: () -> Unit
) {
    val p = item.pedido
    val estStr = p.estado.uppercase()
    val esListo = estStr == "LISTO"
    val esRecibido = estStr == "RECIBIDO"

    val estadoEnum = try {
        EstadoPedido.valueOf(estStr)
    } catch (e: Exception) {
        EstadoPedido.RECIBIDO
    }

    val primerServicio = item.detalles.firstOrNull()?.servicio?.nombre ?: ""

    val resumenDetalle = item.detalles.joinToString(" + ") { d ->
        val unStr = if (d.servicio.unidad.equals("KG", ignoreCase = true)) "kg" else "prendas"
        "${d.detalle.cantidad} $unStr"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = if (esListo) 1.5.dp else 1.dp,
            color = if (esListo) Color(0xFF22C55E) else Color(0xFFF1F5F9)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconoServicio(nombreServicio = primerServicio)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Pedido #${String.format(Locale.US, "%03d", p.id)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(resumenDetalle, fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Text(
                    String.format(Locale.US, "S/ %.2f", p.total),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ColorEstudiante
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EstadoPedidoChip(estado = estadoEnum)

                if (esListo && !p.casillero.isNullOrBlank()) {
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Casillero ${p.casillero}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (esRecibido) {
                    Text(
                        "Cancelar",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { onCancelar() }
                    )
                } else {
                    Text(
                        "Entrega ${p.fechaEntrega}",
                        fontSize = 11.sp,
                        color = ColorEstudiante,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun BarraInferiorEstudiante(
    rutaSeleccionada: String,
    onNavegar: (String) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.INICIO_ESTUDIANTE,
            onClick = { onNavegar(Rutas.INICIO_ESTUDIANTE) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ColorEstudiante, indicatorColor = Color(0xFFE0F2FE))
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.MIS_PEDIDOS,
            onClick = { onNavegar(Rutas.MIS_PEDIDOS) },
            icon = { Icon(Icons.Default.ListAlt, contentDescription = "Pedidos") },
            label = { Text("Pedidos", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ColorEstudiante, indicatorColor = Color(0xFFE0F2FE))
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.TARIFAS,
            onClick = { onNavegar(Rutas.TARIFAS) },
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = "Tarifas") },
            label = { Text("Tarifas", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ColorEstudiante, indicatorColor = Color(0xFFE0F2FE))
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
    }
}