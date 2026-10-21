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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(end = 48.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
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
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo pedido", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
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
                color = MaterialTheme.colorScheme.surfaceVariant
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
                                color = if (pestanaSeleccionada == 0) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                                color = if (pestanaSeleccionada == 1) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                                Text(
                                    "Aviso",
                                    fontWeight = FontWeight.Bold,
                                    color = ColorEstudiante,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Tu pedido #${String.format(Locale.US, "%03d", pedidoListo.pedido.id)} está listo; recógelo en el casillero indicado.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    lineHeight = 16.sp
                                )
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
    val esEnProceso = estStr == "EN_PROCESO" || estStr == "EN PROCESO"

    val estadoEnum = try {
        EstadoPedido.valueOf(estStr)
    } catch (e: Exception) {
        EstadoPedido.RECIBIDO
    }

    val primerServicio = item.detalles.firstOrNull()?.servicio?.nombre ?: ""

    val resumenDetalle = if (item.detalles.isNotEmpty()) {
        item.detalles.joinToString(" + ") { d ->
            val cantFormatted = if (d.detalle.cantidad % 1.0 == 0.0) {
                d.detalle.cantidad.toInt().toString()
            } else {
                d.detalle.cantidad.toString()
            }
            val unStr = if (d.servicio.unidad.equals("KG", ignoreCase = true)) "kg" else "prendas"
            "$cantFormatted $unStr"
        }
    } else {
        formatearFechaCorta(p.fechaRegistro)
    }

    val nombreCliente = item.usuario?.nombre?.let { nombre ->
        val partes = nombre.trim().split(" ")
        if (partes.size >= 2) "${partes[0]} ${partes[1].take(1)}." else partes[0]
    } ?: "Ana G."

    val subTextoCard = if (resumenDetalle.isBlank()) nombreCliente else "$nombreCliente  ·  $resumenDetalle"

    val (bordeTarjeta, fondoIcono, colorIcono) = when {
        esListo -> Triple(Color(0xFF22C55E), Color(0xFFDCFCE7), Color(0xFF16A34A))
        esEnProceso -> Triple(Color(0xFF818CF8), Color(0xFFEEF2FF), Color(0xFF4F46E5))
        else -> Triple(Color(0xFFFDE68A), Color(0xFFFEF3C7), Color(0xFFD97706))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(width = 1.2.dp, color = bordeTarjeta)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(42.dp)) {
                        IconoServicio(
                            nombreServicio = primerServicio,
                            backgroundColor = fondoIcono,
                            tintColor = colorIcono
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Pedido #${String.format(Locale.US, "%03d", p.id)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            subTextoCard,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    String.format(Locale.US, "S/ %.2f", p.total),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ColorEstudiante
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EstadoPedidoChip(estado = estadoEnum)

                if (esListo && !p.casillero.isNullOrBlank()) {
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Casillero ${p.casillero}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else if (esRecibido) {
                    Text(
                        "Cancelar",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { onCancelar() }
                    )
                } else {
                    Text(
                        "Entrega ${formatearFechaEntregaPrototipo(p.fechaEntrega)}",
                        fontSize = 11.sp,
                        color = ColorEstudiante,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun formatearFechaEntregaPrototipo(fechaRaw: String): String {
    return try {
        val parsed = LocalDate.parse(fechaRaw)
        val formatter = DateTimeFormatter.ofPattern("EEE d", Locale("es", "ES"))
        parsed.format(formatter)
    } catch (e: Exception) {
        fechaRaw
    }
}

private fun formatearFechaCorta(fechaRaw: String): String {
    return try {
        val parsed = LocalDate.parse(fechaRaw)
        val formatter = DateTimeFormatter.ofPattern("EEE d MMM", Locale("es", "ES"))
        parsed.format(formatter)
    } catch (e: Exception) {
        fechaRaw
    }
}

@Composable
private fun BarraInferiorEstudiante(
    rutaSeleccionada: String,
    onNavegar: (String) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.INICIO_ESTUDIANTE,
            onClick = { onNavegar(Rutas.INICIO_ESTUDIANTE) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorEstudiante,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                selectedTextColor = ColorEstudiante,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                indicatorColor = ColorEstudiante.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.MIS_PEDIDOS,
            onClick = { onNavegar(Rutas.MIS_PEDIDOS) },
            icon = { Icon(Icons.Default.ListAlt, contentDescription = "Pedidos") },
            label = { Text("Pedidos", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorEstudiante,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                selectedTextColor = ColorEstudiante,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                indicatorColor = ColorEstudiante.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.TARIFAS,
            onClick = { onNavegar(Rutas.TARIFAS) },
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = "Tarifas") },
            label = { Text("Tarifas", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorEstudiante,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                selectedTextColor = ColorEstudiante,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                indicatorColor = ColorEstudiante.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.PERFIL_ESTUDIANTE,
            onClick = { onNavegar(Rutas.PERFIL_ESTUDIANTE) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorEstudiante,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                selectedTextColor = ColorEstudiante,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                indicatorColor = ColorEstudiante.copy(alpha = 0.15f)
            )
        )
    }
}