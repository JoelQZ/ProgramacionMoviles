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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaHoraScreen(
    navController: NavController,
    medicoId: String
) {
    val medico = Repositorio.obtenerMedicoPorId(medicoId)
    val context = LocalContext.current

    var diaSeleccionado by remember { mutableStateOf(16) }
    var horaSeleccionada by remember { mutableStateOf("09:30") }

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
            Text("Setiembre 2026", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(15, 16, 17, 18, 19).forEach { dia ->
                    val esSeleccionado = dia == diaSeleccionado
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                if (esSeleccionado) Color(0xFF2563EB) else Color(0xFFF1F5F9),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { diaSeleccionado = dia },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Día", fontSize = 10.sp, color = if (esSeleccionado) Color.White else Color.Gray)
                            Text("$dia", fontWeight = FontWeight.Bold, color = if (esSeleccionado) Color.White else Color.Black)
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
                    val fechaFormateada = "$diaSeleccionado-09-2026"
                    if (horaSeleccionada.isEmpty()) {
                        Toast.makeText(context, "Seleccione un horario", Toast.LENGTH_SHORT).show()
                    } else {
                        navController.navigate(Rutas.ConfirmarCita.crearRuta(medicoId, fechaFormateada, horaSeleccionada))
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