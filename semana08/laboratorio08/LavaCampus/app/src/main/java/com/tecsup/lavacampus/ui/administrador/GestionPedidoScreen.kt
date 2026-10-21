package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import com.tecsup.lavacampus.ui.theme.ColorAdministrador
import com.tecsup.lavacampus.viewmodel.GestionPedidoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPedidoScreen(
    viewModel: GestionPedidoViewModel,
    onVolver: () -> Unit
) {
    val item by viewModel.pedidoConDetalle.collectAsState()
    val ocupados by viewModel.casillerosOcupados.collectAsState()
    val casilleroElegido by viewModel.casilleroElegido.collectAsState()
    val estaPagado by viewModel.estaPagado.collectAsState()
    val errorMsg by viewModel.mensajeError.collectAsState()

    val todosCasilleros = remember { (1..8).map { String.format(Locale.US, "C-%02d", it) } }
    val esOscuro = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Gestionar pedido #${item?.pedido?.id?.let { String.format(Locale.US, "%03d", it) } ?: ""}",
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
        if (item == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ColorAdministrador)
            }
        } else {
            val p = item!!.pedido
            val u = item!!.usuario
            val estUpper = p.estado.uppercase()

            val estadoEnum = try {
                EstadoPedido.valueOf(estUpper)
            } catch (e: Exception) {
                EstadoPedido.RECIBIDO
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(42.dp),
                                shape = CircleShape,
                                color = ColorAdministrador.copy(alpha = 0.15f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        u?.nombre?.take(2)?.uppercase() ?: "ES",
                                        fontWeight = FontWeight.Bold,
                                        color = ColorAdministrador
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    u?.nombre ?: "Estudiante",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    u?.codigo ?: "",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            EstadoPedidoChip(estado = estadoEnum)
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            StepperEstado(
                                estadoActual = estadoEnum,
                                colorActivo = ColorAdministrador
                            )
                        }
                    }
                }

                if (estUpper == "EN_PROCESO" || estUpper == "LISTO") {
                    item {
                        Column {
                            Text(
                                "Casillero para el recojo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.height(110.dp)
                            ) {
                                items(todosCasilleros) { cas ->
                                    val estaOcupado = ocupados.contains(cas) && cas != p.casillero
                                    val esSeleccionado = casilleroElegido == cas

                                    FilterChip(
                                        selected = esSeleccionado,
                                        enabled = !estaOcupado,
                                        onClick = { viewModel.elegirCasillero(cas) },
                                        label = { Text(cas, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ColorAdministrador,
                                            selectedLabelColor = Color.White,
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            labelColor = MaterialTheme.colorScheme.onSurface,
                                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Total S/ ${String.format(Locale.US, "%.2f", p.total)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Se cobra al entregar",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Cobrado",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = estaPagado,
                                    onCheckedChange = { viewModel.marcarPagado(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = ColorAdministrador
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    val fondoRegla = if (esOscuro) ColorAdministrador.copy(alpha = 0.2f) else Color(0xFFF3EDF7)
                    val textoRegla = if (esOscuro) Color(0xFFD8B4FE) else ColorAdministrador

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = fondoRegla)
                    ) {
                        Text(
                            text = "Regla: no se entrega un pedido sin cobrar, ni se pasa a Listo sin casillero.",
                            fontSize = 11.sp,
                            color = textoRegla,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (errorMsg != null) {
                    item {
                        Text(
                            errorMsg!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (estUpper != "ENTREGADO" && estUpper != "CANCELADO") {
                    item {
                        val textoBoton = when (estUpper) {
                            "RECIBIDO" -> "Iniciar pedido"
                            "EN_PROCESO" -> "Marcar como Listo"
                            "LISTO" -> "Entregar pedido"
                            else -> "Avanzar estado"
                        }

                        Button(
                            onClick = { viewModel.avanzar() },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorAdministrador),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(textoBoton, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        }

                        if (estUpper == "RECIBIDO" || estUpper == "EN_PROCESO") {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { viewModel.cancelar() },
                                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("Cancelar pedido", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}