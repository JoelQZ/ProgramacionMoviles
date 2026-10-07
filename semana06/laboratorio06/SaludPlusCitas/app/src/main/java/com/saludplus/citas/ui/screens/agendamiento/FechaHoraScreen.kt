package com.saludplus.citas.ui.screens.agendamiento

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaHoraScreen(
    navController: NavController,
    medicoId: String
) {
    val medico = Repositorio.obtenerMedicoPorId(medicoId)
    val context = LocalContext.current

    var semanaOffset by remember { mutableStateOf(0L) }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }
    var horaSeleccionada by remember { mutableStateOf("") }

    val diasHabiles = remember(semanaOffset) {
        val inicioBase = LocalDate.now().plusWeeks(semanaOffset)
        var fechaLoop = if (semanaOffset == 0L) LocalDate.now() else inicioBase.with(DayOfWeek.MONDAY)
        val lista = mutableListOf<LocalDate>()

        while (lista.size < 5) {
            if (fechaLoop.dayOfWeek != DayOfWeek.SATURDAY && fechaLoop.dayOfWeek != DayOfWeek.SUNDAY) {
                if (fechaLoop >= LocalDate.now()) {
                    lista.add(fechaLoop)
                }
            }
            fechaLoop = fechaLoop.plusDays(1)
        }
        lista
    }

    LaunchedEffect(diasHabiles) {
        if (fechaSeleccionada == null || fechaSeleccionada !in diasHabiles) {
            fechaSeleccionada = diasHabiles.firstOrNull()
            horaSeleccionada = ""
        }
    }

    val mesAñoTexto = remember(diasHabiles) {
        val primeraFecha = diasHabiles.firstOrNull() ?: LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
        primeraFecha.format(formatter).replaceFirstChar { it.uppercase() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar fecha y hora", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFE2E8F0), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(medico?.nombre ?: "Dra. Ana Torres", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(medico?.especialidadNombre ?: "Especialista", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (semanaOffset > 0) semanaOffset-- },
                    enabled = semanaOffset > 0
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Semana anterior",
                        tint = if (semanaOffset > 0) Color.Black else Color.LightGray
                    )
                }

                Text(mesAñoTexto, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                IconButton(onClick = { semanaOffset++ }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Semana siguiente")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                diasHabiles.forEach { fecha ->
                    val esSeleccionado = fecha == fechaSeleccionada
                    val nombreDia = fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
                        .replaceFirstChar { it.uppercase() }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (esSeleccionado) Color(0xFF2563EB) else Color(0xFFF1F5F9),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                fechaSeleccionada = fecha
                                horaSeleccionada = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = nombreDia,
                                fontSize = 10.sp,
                                color = if (esSeleccionado) Color.White else Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${fecha.dayOfMonth}",
                                fontWeight = FontWeight.Bold,
                                color = if (esSeleccionado) Color.White else Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Horarios disponibles", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))

            val horas = listOf("08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                horas.chunked(3).forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        fila.forEach { h ->
                            val esHoraSel = h == horaSeleccionada
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .background(
                                        if (esHoraSel) Color(0xFF2563EB) else Color.White,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(1.dp, if (esHoraSel) Color(0xFF2563EB) else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                    .clickable { horaSeleccionada = h },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(h, color = if (esHoraSel) Color.White else Color.Black, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (fechaSeleccionada == null) {
                        Toast.makeText(context, "Seleccione un día", Toast.LENGTH_SHORT).show()
                    } else if (horaSeleccionada.isEmpty()) {
                        Toast.makeText(context, "Seleccione un horario", Toast.LENGTH_SHORT).show()
                    } else {
                        val fechaEnvio = fechaSeleccionada.toString()
                        navController.navigate(Rutas.ConfirmarCita.crearRuta(medicoId, fechaEnvio, horaSeleccionada))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Continuar", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}