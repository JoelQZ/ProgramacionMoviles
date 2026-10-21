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
    val subtotal by viewModel.subtotal.collectAsState()
    val descuento by viewModel.descuento.collectAsState()
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
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
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
                    Spacer(modifier = Modifier.height(2.dp))
                    OutlinedTextField(
                        value = nota,
                        onValueChange = { viewModel.onNotaChanged(it) },
                        label = { Text("Nota (opcional)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        placeholder = { Text("Ropa de color separada", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedBorderColor = ColorEstudiante,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedLabelColor = ColorEstudiante,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (hayServiciosSeleccionados) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, ColorEstudiante.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Resumen",
                                    fontWeight = FontWeight.Bold,
                                    color = ColorEstudiante,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                if (descuento > 0.0) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFDCFCE7)
                                    ) {
                                        Text(
                                            text = "🎉 ¡10% de descuento aplicado por superar los 5 kg!",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                servicios.forEach { s ->
                                    val cant = cantidades[s.id] ?: 0.0
                                    if (cant > 0.0) {
                                        val itemSubtotal = cant * s.precio
                                        val esKg = s.unidad.equals("KG", ignoreCase = true)

                                        val textoCant = if (esKg) {
                                            String.format(Locale.US, "%.1f kg", cant)
                                        } else {
                                            val cInt = cant.toInt()
                                            val uNorm = s.unidad.lowercase()
                                            if (uNorm.contains("und") || uNorm.contains("unidad")) {
                                                "$cInt und"
                                            } else if (cInt == 1) {
                                                "1 $uNorm"
                                            } else {
                                                "$cInt ${uNorm}s"
                                            }
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "${s.nombre} $textoCant",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                String.format(Locale.US, "S/ %.2f", itemSubtotal),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                if (descuento > 0.0) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant
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
                                            String.format(Locale.US, "S/ %.2f", subtotal),
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
                                            "Descuento 10% (>5 kg)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF16A34A)
                                        )
                                        Text(
                                            String.format(Locale.US, "-S/ %.2f", descuento),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF16A34A)
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Total",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Button(
                    onClick = { viewModel.confirmarPedido(onPedidoConfirmado) },
                    enabled = hayServiciosSeleccionados,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorEstudiante,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp)
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
    val estaActivo = cantidad > 0.0

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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (estaActivo) 2.dp else 1.dp,
            color = if (estaActivo) ColorEstudiante else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (estaActivo) ColorEstudiante.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant),
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
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = String.format(Locale.US, "S/ %.2f por %s · %s", servicio.precio, servicio.unidad.lowercase(), horasTexto),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (estaActivo) ColorEstudiante else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(enabled = estaActivo) { onDecrementar() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Restar",
                        tint = if (estaActivo) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = if (servicio.unidad.equals("KG", ignoreCase = true)) {
                        if (cantidad == 0.0) "0" else String.format(Locale.US, "%.1f kg", cantidad)
                    } else {
                        "${cantidad.toInt()}"
                    },
                    modifier = Modifier.padding(horizontal = 10.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
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