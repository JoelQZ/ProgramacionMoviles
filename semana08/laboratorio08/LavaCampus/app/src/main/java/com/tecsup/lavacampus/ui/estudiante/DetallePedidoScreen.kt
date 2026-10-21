package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalLaundryService
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatearFechaLegible(milisegundos: Long): String {
    return try {
        val sdf = SimpleDateFormat("EEE d MMM", Locale("es", "ES"))
        sdf.format(Date(milisegundos))
    } catch (e: Exception) {
        "Reciente"
    }
}

private fun formatearHoraPaso(milisegundos: Long): String {
    return try {
        val sdf = SimpleDateFormat("d MMM HH:mm", Locale("es", "ES"))
        sdf.format(Date(milisegundos))
    } catch (e: Exception) {
        "20 oct 10:15"
    }
}

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
        containerColor = MaterialTheme.colorScheme.background
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

            val fechaLegible = formatearFechaLegible(item.pedido.fechaCreacion)
            val horaPaso1 = formatearHoraPaso(item.pedido.fechaCreacion)

            val fechasMap = mapOf(
                EstadoPedido.RECIBIDO to horaPaso1,
                EstadoPedido.EN_PROCESO to if (estadoEnum == EstadoPedido.EN_PROCESO || estadoEnum == EstadoPedido.LISTO || estadoEnum == EstadoPedido.ENTREGADO) horaPaso1 else "Pendiente",
                EstadoPedido.LISTO to if (estadoEnum == EstadoPedido.LISTO || estadoEnum == EstadoPedido.ENTREGADO) horaPaso1 else "Pendiente",
                EstadoPedido.ENTREGADO to if (estadoEnum == EstadoPedido.ENTREGADO) horaPaso1 else "Pendiente"
            )

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
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = ColorEstudiante.copy(alpha = 0.15f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.LocalLaundryService,
                                            contentDescription = null,
                                            tint = ColorEstudiante,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = "Registrado el $fechaLegible",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Entrega estimada: ${item.pedido.fechaEntrega}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    EstadoPedidoChip(estado = estadoEnum)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Seguimiento",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                StepperEstado(
                                    estadoActual = estadoEnum,
                                    fechasEtapas = fechasMap
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Detalle",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                item.detalles.forEach { d ->
                                    val cantFormatted = if (d.servicio.unidad.equals("KG", ignoreCase = true)) {
                                        if (d.detalle.cantidad % 1.0 == 0.0) {
                                            "${d.detalle.cantidad.toInt()} kg"
                                        } else {
                                            String.format(Locale.US, "%.1f kg", d.detalle.cantidad)
                                        }
                                    } else {
                                        val cInt = d.detalle.cantidad.toInt()
                                        val uNorm = d.servicio.unidad.lowercase(Locale.getDefault())
                                        val unStr = if (uNorm.contains("und") || uNorm.contains("unidad")) {
                                            "und"
                                        } else if (cInt == 1) {
                                            uNorm
                                        } else {
                                            "${uNorm}s"
                                        }
                                        "$cInt $unStr"
                                    }

                                    val precioUnitarioStr = String.format(Locale.US, "S/ %.2f", d.servicio.precio)
                                    val textoIzquierda = "${d.servicio.nombre} · $cantFormatted × $precioUnitarioStr"

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = textoIzquierda,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = String.format(Locale.US, "S/ %.2f", d.detalle.subtotal),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                val subtotalCalculado = item.detalles.sumOf { it.detalle.subtotal }
                                val desc = item.pedido.descuento

                                if (desc > 0.0) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Subtotal",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            String.format(Locale.US, "S/ %.2f", subtotalCalculado),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Descuento (volumen > 5 kg)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF16A34A)
                                        )
                                        Text(
                                            String.format(Locale.US, "-S/ %.2f", desc),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF16A34A)
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Total",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
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

                        val esOscuro = isSystemInDarkTheme()
                        val fondoPago = if (estaPagado) {
                            if (esOscuro) Color(0xFF064E3B) else Color(0xFFE8F5E9)
                        } else {
                            if (esOscuro) Color(0xFF78350F) else Color(0xFFFFF8E1)
                        }
                        val textoPago = if (estaPagado) {
                            if (esOscuro) Color(0xFFA7F3D0) else Color(0xFF2E7D32)
                        } else {
                            if (esOscuro) Color(0xFFFDE68A) else Color(0xFFE65100)
                        }
                        val textoSubCasillero = if (estaPagado) {
                            if (esOscuro) Color(0xFFA7F3D0) else Color(0xFF166534)
                        } else {
                            if (esOscuro) Color(0xFFFDE68A) else Color(0xFF78350F)
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = fondoPago)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (estaPagado) "Pago realizado" else "Pago pendiente - se paga al recoger",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = textoPago
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = textoCasillero,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textoSubCasillero
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
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (puedeCancelar) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFEF4444),
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    ) {
                        Text("Cancelar pedido", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    if (!puedeCancelar && estadoEnum != EstadoPedido.CANCELADO) {
                        Text(
                            text = "Solo se cancela antes de empezar el lavado",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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