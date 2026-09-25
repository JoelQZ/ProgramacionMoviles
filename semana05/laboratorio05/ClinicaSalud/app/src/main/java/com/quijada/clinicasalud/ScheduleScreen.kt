package com.quijada.clinicasalud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScheduleScreen(
    medicoId: Int,
    onConfirmarClick: () -> Unit
) {
    val medico = Repository.medicos.find { it.id == medicoId } ?: Repository.medicos.first()

    var selectedFecha by remember { mutableStateOf("Viernes 27") }
    var selectedHora by remember { mutableStateOf("10:30 am") }

    val fechas = listOf("Viernes 27", "Sábado 28", "Lunes 30")
    val horas = listOf("09:00 am", "10:30 am", "02:00 pm", "04:30 pm")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Agendar cita con", fontSize = 14.sp, color = Color.Gray)
        Text(medico.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Selecciona una fecha", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(fechas) { fecha ->
                FilterChip(
                    selected = selectedFecha == fecha,
                    onClick = { selectedFecha = fecha },
                    label = { Text(fecha) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Selecciona una hora", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(horas) { hora ->
                FilterChip(
                    selected = selectedHora == hora,
                    onClick = { selectedHora = hora },
                    label = { Text(hora) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                Repository.citas.add(
                    Cita(
                        id = Repository.citas.size + 1,
                        medicoNombre = medico.nombre,
                        especialidad = medico.especialidad,
                        fecha = selectedFecha,
                        hora = selectedHora,
                        estado = "Confirmada"
                    )
                )
                onConfirmarClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Confirmar cita", fontSize = 16.sp)
        }
    }
}