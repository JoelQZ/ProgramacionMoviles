package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.ui.theme.ColorEstudiante
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoPedidoScreen(
    viewModel: NuevoPedidoViewModel,
    onVolver: () -> Unit,
    onPedidoConfirmado: () -> Unit
) {
    val servicios by viewModel.serviciosActivos.collectAsState()
    val cantidades by viewModel.cantidades.collectAsState()
    val nota by viewModel.nota.collectAsState()
    val total by viewModel.total.collectAsState()
    val fechaEstimada by viewModel.fechaEntregaEstimada.collectAsState()
    val errorMsg by viewModel.mensajeError.collectAsState()

    val hayServiciosSeleccionados = cantidades.any { it.value > 0.0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Nuevo pedido",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "¿Qué necesitas?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                }

                items(servicios) { servicio ->
                    val cantidad = cantidades[servicio.id] ?: 0.0
                    ItemServicioPedido(
                        servicio = servicio,
                        cantidad = cantidad,
                        onIncrementar = {
                            val paso = if (servicio.unidad.equals("KG", ignoreCase = true)) 0.5 else 1.0
                            viewModel.cambiarCantidad(servicio, paso)
                        },
                        onDecrementar = {
                            val paso = if (servicio.unidad.equals("KG", ignoreCase = true)) -0.5 else -1.0
                            viewModel.cambiarCantidad(servicio, paso)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = nota,
                        onValueChange = { viewModel.onNotaChanged(it) },
                        label = { Text("Nota (opcional)", fontSize = 13.sp) },
                        placeholder = { Text("Ropa de color separada", fontSize = 13.sp, color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedBorderColor = ColorEstudiante
                        )
                    )
                }

                if (hayServiciosSeleccionados) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE0F7FA)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFB2EBF2))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Resumen",
                                    fontWeight = FontWeight.Bold,
                                    color = ColorEstudiante,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                servicios.forEach { s ->
                                    val cant = cantidades[s.id] ?: 0.0
                                    if (cant > 0.0) {
                                        val subtotal = cant * s.precio
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            val textoCant = if (s.unidad.equals("KG", ignoreCase = true)) "$cant kg" else "${cant.toInt()} prendas"
                                            Text("${s.nombre} $textoCant", fontSize = 13.sp, color = Color.DarkGray)
                                            Text(
                                                String.format(Locale.US, "S/ %.2f", subtotal),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    color = Color(0xFFE2E8F0)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(
                                        String.format(Locale.US, "S/ %.2f", total),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = ColorEstudiante
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (errorMsg != null) {
                Text(
                    text = errorMsg!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = fechaEstimada,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Button(
                    onClick = { viewModel.confirmarPedido(onPedidoConfirmado) },
                    enabled = hayServiciosSeleccionados,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorEstudiante,
                        disabledContainerColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text("Confirmar pedido", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ItemServicioPedido(
    servicio: Servicio,
    cantidad: Double,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit
) {
    val estaActivo = cantidad > 0

    val iconoServicio: ImageVector = when {
        servicio.nombre.contains("Planchado", ignoreCase = true) -> Icons.Default.Checkroom
        servicio.nombre.contains("Seco", ignoreCase = true) -> Icons.Default.DryCleaning
        else -> Icons.Default.LocalLaundryService
    }

    val horasTexto = if (servicio.nombre.contains("Planchado", ignoreCase = true) || servicio.nombre.contains("Seco", ignoreCase = true)) "48 h" else "24 h"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = if (estaActivo) 2.dp else 1.dp,
            color = if (estaActivo) ColorEstudiante else Color(0xFFF1F5F9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (estaActivo) 1.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (estaActivo) Color(0xFFE0F2FE) else Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoServicio,
                    contentDescription = null,
                    tint = ColorEstudiante,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = servicio.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = String.format(Locale.US, "S/ %.2f por %s · %s", servicio.precio, servicio.unidad.lowercase(), horasTexto),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (estaActivo) ColorEstudiante else Color(0xFFE2E8F0))
                        .clickable(enabled = estaActivo) { onDecrementar() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Restar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = if (servicio.unidad.equals("KG", ignoreCase = true)) {
                        if (cantidad == 0.0) "0" else "$cantidad kg"
                    } else {
                        "${cantidad.toInt()}"
                    },
                    modifier = Modifier.padding(horizontal = 10.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ColorEstudiante)
                        .clickable { onIncrementar() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Sumar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}