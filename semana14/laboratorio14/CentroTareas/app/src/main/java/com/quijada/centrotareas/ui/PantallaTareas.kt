package com.quijada.centrotareas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Reorder
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quijada.centrotareas.model.CargaUi
import com.quijada.centrotareas.model.ComparacionFila
import com.quijada.centrotareas.model.PrimosUi
import com.quijada.centrotareas.viewmodel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(
    viewModel: TareasViewModel,
    modifier: Modifier = Modifier
) {
    val mensaje by viewModel.mensaje.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val enProgreso by viewModel.enProgreso.collectAsState()
    val primos by viewModel.primos.collectAsState()
    val progresoPrimos by viewModel.progresoPrimos.collectAsState()
    val carga by viewModel.carga.collectAsState()
    val simularError by viewModel.simularError.collectAsState()
    val limitarTiempo by viewModel.limitarTiempo.collectAsState()
    val tablaComparacion by viewModel.tablaComparacion.collectAsState()
    val historial by viewModel.historial.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF5F7FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER OSCURO ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34D399))
                    )
                    Text(
                        text = "● PANEL DE OPERACIONES",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "CentroTareas",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Control y ejecución",
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp
                )
            }

            // --- CONTENIDO PRINCIPAL ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Sección de herramientas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Herramientas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = "4 MÓDULOS",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Tarjeta 1: Tarea con progreso
                TarjetaTareaConProgresoFigma(
                    mensaje = mensaje,
                    progreso = progreso,
                    enProgreso = enProgreso,
                    onTareaLarga = { viewModel.tareaLarga() },
                    onCancelarTarea = { viewModel.cancelarTarea() }
                )

                // Tarjeta 2: Contar números primos
                TarjetaContarPrimosFigma(
                    primosState = primos,
                    progresoPrimos = progresoPrimos,
                    onCalcularPrimos = { hasta -> viewModel.calcularPrimos(hasta) },
                    onCancelarPrimos = { viewModel.cancelarPrimos() }
                )

                // Tarjeta 3: Dos cargas
                TarjetaDosCargasFigma(
                    cargaState = carga,
                    simularError = simularError,
                    limitarTiempo = limitarTiempo,
                    tablaComparacion = tablaComparacion,
                    onCambiarSimularError = { viewModel.cambiarSimularError(it) },
                    onCambiarLimitarTiempo = { viewModel.cambiarLimitarTiempo(it) },
                    onCargarUnaPorUna = { viewModel.cargarUnaPorUna() },
                    onCargarALaVez = { viewModel.cargarALaVez() },
                    onReintentar = { viewModel.cargarALaVez() }
                )

                // Tarjeta 4: Historial reciente (Mejora 5)
                TarjetaHistorialFigma(
                    historial = historial,
                    onLimpiarHistorial = { viewModel.limpiarHistorial() }
                )

                // --- FOOTER ---
                Text(
                    text = "CENTROTAREAS / CONTROL OPERATIVO",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}

@Composable
fun TarjetaTareaConProgresoFigma(
    mensaje: String,
    progreso: Float,
    enProgreso: Boolean,
    onTareaLarga: () -> Unit,
    onCancelarTarea: () -> Unit
) {
    var contador by remember { mutableIntStateOf(0) }

    val statusBg = when (mensaje) {
        "Cancelado" -> Color(0xFFFEE2E2)
        "Terminó" -> Color(0xFFECFDF5)
        "Trabajando..." -> Color(0xFFDBEAFE)
        else -> Color(0xFFF1F5F9)
    }
    val statusText = when (mensaje) {
        "Cancelado" -> Color(0xFFDC2626)
        "Terminó" -> Color(0xFF059669)
        "Trabajando..." -> Color(0xFF2563EB)
        else -> Color(0xFF64748B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BarChart,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "Tarea con progreso",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Supervisa cada ejecución",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Contenedor interno gris azulado suave
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(progreso * 100).toInt()} %",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = mensaje.uppercase(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusText
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF2563EB),
                    trackColor = Color(0xFFE2E8F0)
                )

                Text(
                    text = "Progreso de ejecución",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Interacciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Interacciones",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Contador manual",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.clickable { contador++ }
                ) {
                    Text(
                        text = "Tócame: $contador",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                }
            }

            // Botones inferiores al 50%
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onTareaLarga,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "Iniciar", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                OutlinedButton(
                    onClick = onCancelarTarea,
                    enabled = enProgreso,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (enProgreso) Color(0xFFFEE2E2) else Color(0xFFF1F5F9),
                        contentColor = if (enProgreso) Color(0xFFDC2626) else Color(0xFF64748B),
                        disabledContainerColor = Color(0xFFF1F5F9),
                        disabledContentColor = Color(0xFF94A3B8)
                    ),
                    border = null
                ) {
                    Text(text = "Cancelar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TarjetaContarPrimosFigma(
    primosState: PrimosUi,
    progresoPrimos: Float,
    onCalcularPrimos: (Int) -> Unit,
    onCancelarPrimos: () -> Unit
) {
    var textoNumero by remember { mutableStateOf("500000") }
    val isCalculating = primosState is PrimosUi.Calculando

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3E8FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Calculate,
                        contentDescription = null,
                        tint = Color(0xFF7C3AED),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "Contar números primos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Configura el rango del cálculo",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Fila etiqueta y píldora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hasta el número",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Surface(
                    color = Color(0xFFF3E8FF),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "LÍMITE",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED)
                    )
                }
            }

            // Campo numérico
            OutlinedTextField(
                value = textoNumero,
                onValueChange = { textoNumero = it },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3E8FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "#", fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7C3AED),
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                ),
                singleLine = true
            )

            Text(
                text = "Define el valor máximo que quieres analizar.",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

            // Botones al 50%
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val hasta = textoNumero.toIntOrNull() ?: 0
                        onCalcularPrimos(hasta)
                    },
                    enabled = !isCalculating,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text(text = "Contar", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onCancelarPrimos,
                    enabled = isCalculating,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = Color(0xFF64748B)
                    ),
                    border = null
                ) {
                    Text(text = "Cancelar", fontWeight = FontWeight.Bold)
                }
            }

            // Resultados / Cargando / Cancelado
            when (primosState) {
                is PrimosUi.Inactivo -> {}
                is PrimosUi.Calculando -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3E8FF))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color(0xFF7C3AED),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Calculando primos...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C3AED)
                                )
                            }
                            Text(
                                text = "Calculando: ${(progresoPrimos * 100).toInt()} %",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C3AED)
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progresoPrimos },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF7C3AED),
                            trackColor = Color(0xFFE9D5FF)
                        )
                    }
                }
                is PrimosUi.Listo -> {
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Primos hasta ${primosState.hasta}: ${primosState.cantidad}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF059669)
                            )
                            Text(
                                text = "Tiempo de ejecución: ${primosState.duracionMs} ms",
                                fontSize = 13.sp,
                                color = Color(0xFF059669)
                            )
                        }
                    }
                }
                is PrimosUi.Cancelado -> {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Cálculo cancelado",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaDosCargasFigma(
    cargaState: CargaUi,
    simularError: Boolean,
    limitarTiempo: Boolean,
    tablaComparacion: List<ComparacionFila>,
    onCambiarSimularError: (Boolean) -> Unit,
    onCambiarLimitarTiempo: (Boolean) -> Unit,
    onCargarUnaPorUna: () -> Unit,
    onCargarALaVez: () -> Unit,
    onReintentar: () -> Unit
) {
    val isLoading = cargaState is CargaUi.Cargando
    var modoSeleccionado by remember { mutableStateOf("A la vez") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFECFDF5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Layers,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "Dos cargas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Elige cómo ejecutar las cargas",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Text(
                text = "MODO DE EJECUCIÓN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.8.sp
            )

            // Tarjetas seleccionables lado a lado (50%)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Una por una
                val isUnaPorUnaSelected = modoSeleccionado == "Una por una"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isUnaPorUnaSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                        .border(
                            width = if (isUnaPorUnaSelected) 2.dp else 1.dp,
                            color = if (isUnaPorUnaSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            modoSeleccionado = "Una por una"
                            onCargarUnaPorUna()
                        }
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Reorder,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                            if (isUnaPorUnaSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2563EB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = "Una por una",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Secuencial",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // A la vez
                val isALaVezSelected = modoSeleccionado == "A la vez"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isALaVezSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                        .border(
                            width = if (isALaVezSelected) 2.dp else 1.dp,
                            color = if (isALaVezSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            modoSeleccionado = "A la vez"
                            onCargarALaVez()
                        }
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SwapVert,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                            if (isALaVezSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2563EB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = "A la vez",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Paralelo (async)",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Simular error switch container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Simular error",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Prueba el manejo de fallos",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Switch(
                    checked = simularError,
                    onCheckedChange = onCambiarSimularError,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2563EB)
                    )
                )
            }

            // Límite de tiempo switch container (Mejora 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Límite de tiempo (2 s)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Cancela si supera 2000 ms con withTimeout",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Switch(
                    checked = limitarTiempo,
                    onCheckedChange = onCambiarLimitarTiempo,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2563EB)
                    )
                )
            }

            // Banner informativo de tiempo
            Surface(
                color = Color(0xFFECFDF5),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "«Una por una» tarda ≈ 5000 ms",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF059669)
                    )
                }
            }

            // Estados de carga / éxito / error
            if (isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF2563EB)
                    )
                    Text(
                        text = "Ejecutando operaciones en segundo plano...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2563EB)
                    )
                }
            }

            when (cargaState) {
                is CargaUi.Idle -> {}
                is CargaUi.Cargando -> {}
                is CargaUi.Exito -> {
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Modo: ${cargaState.modo}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF059669)
                            )
                            Text(text = "Perfil: ${cargaState.perfil}", fontSize = 13.sp, color = Color(0xFF059669))
                            Text(text = "Pedidos: ${cargaState.cantidadPedidos}", fontSize = 13.sp, color = Color(0xFF059669))
                            Text(text = "Tiempo: ${cargaState.duracionMs} ms", fontSize = 13.sp, color = Color(0xFF059669))
                        }
                    }
                }
                is CargaUi.Error -> {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = cargaState.mensaje,
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Button(
                                onClick = onReintentar,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                            ) {
                                Text(text = "Reintentar", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Tabla de comparación compacta (Mejora 6)
            if (tablaComparacion.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .background(Color.White)
                ) {
                    // Header de la tabla
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "#", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(0.5f))
                        Text(text = "Una por una", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1.2f))
                        Text(text = "A la vez", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1.2f))
                        Text(text = "Ahorro", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                    }

                    // Filas de la tabla
                    tablaComparacion.forEach { fila ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${fila.corrida}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A), modifier = Modifier.weight(0.5f))
                            Text(
                                text = fila.tiempoSecuencialMs?.let { "${it} ms" } ?: "--",
                                fontSize = 12.sp,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = fila.tiempoConcurrenteMs?.let { "${it} ms" } ?: "--",
                                fontSize = 12.sp,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = fila.ahorroMs?.let { "-${it} ms" } ?: "--",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (fila.ahorroMs != null) Color(0xFF16A34A) else Color(0xFF94A3B8),
                                modifier = Modifier.weight(1.1f),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaHistorialFigma(
    historial: List<String>,
    onLimpiarHistorial: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Historial reciente",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Últimos 5 resultados guardados",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                if (historial.isNotEmpty()) {
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.clickable { onLimpiarHistorial() }
                    ) {
                        Text(
                            text = "Limpiar",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }

            if (historial.isEmpty()) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Aún no hay registros guardados",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    historial.forEachIndexed { index, registro ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                                Text(
                                    text = registro,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
