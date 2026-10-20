package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.EstadoPedidoChip
import com.tecsup.lavacampus.ui.components.StepperEstado
import com.tecsup.lavacampus.ui.theme.ColorEstudiante
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePedidoScreen(
    viewModel: DetallePedidoViewModel,
    onVolver: () -> Unit
) {
    val pedidoConDetalle by viewModel.pedido.collectAsState()
    val puedeCancelar by viewModel.puedeCancelar.collectAsState()
    var mostrarDialogoCancelar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Pedido #${pedidoConDetalle?.pedido?.id?.let { String.format(Locale.US, "%03d", it) } ?: ""}",
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
        containerColor = Color.White
    ) { innerPadding ->
        val item = pedidoConDetalle

        if (item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ColorEstudiante)
            }
        } else {
            val estadoEnum = try {
                EstadoPedido.valueOf(item.pedido.estado.uppercase())
            } catch (e: Exception) {
                EstadoPedido.RECIBIDO
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Registrado el ${item.pedido.fechaCreacion}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Entrega estimada: ${item.pedido.fechaEntrega}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                EstadoPedidoChip(estado = estadoEnum)
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Seguimiento",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        StepperEstado(estadoActual = estadoEnum)
                    }

                    item {
                        Text(
                            text = "Detalle",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                item.detalles.forEach { d ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val unidadStr = if (d.servicio.unidad.equals("KG", ignoreCase = true)) "kg" else "prendas"
                                        Text(
                                            text = "${d.servicio.nombre} · ${d.detalle.cantidad} $unidadStr",
                                            fontSize = 13.sp,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = String.format(Locale.US, "S/ %.2f", d.detalle.subtotal),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(
                                        text = String.format(Locale.US, "S/ %.2f", item.pedido.total),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = ColorEstudiante
                                    )
                                }
                            }
                        }
                    }

                    item {
                        val estaPagado = item.pedido.pagado
                        val estaListo = estadoEnum == EstadoPedido.LISTO || estadoEnum == EstadoPedido.ENTREGADO
                        val textoCasillero = if (estaListo && !item.pedido.casillero.isNullOrBlank()) {
                            "Casillero asignado: ${item.pedido.casillero}"
                        } else {
                            "Casillero: se asignará al estar listo"
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (estaPagado) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (estaPagado) "Pago realizado" else "Pago pendiente - se paga al recoger",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (estaPagado) Color(0xFF2E7D32) else Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = textoCasillero,
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedButton(
                        onClick = { mostrarDialogoCancelar = true },
                        enabled = puedeCancelar,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                            disabledContentColor = Color.Gray
                        )
                    ) {
                        Text("Cancelar pedido", fontWeight = FontWeight.Bold)
                    }

                    if (!puedeCancelar && estadoEnum != EstadoPedido.CANCELADO) {
                        Text(
                            text = "Solo se cancela antes de empezar el lavado",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            title = { Text("¿Cancelar pedido?", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas cancelar este pedido?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCancelar = false
                        viewModel.cancelarPedido { onVolver() }
                    }
                ) {
                    Text("Sí, cancelar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCancelar = false }) {
                    Text("Volver")
                }
            }
        )
    }
}