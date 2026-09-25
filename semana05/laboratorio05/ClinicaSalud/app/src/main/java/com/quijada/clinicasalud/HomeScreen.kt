package com.quijada.clinicasalud

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onDoctorSelect: (Int) -> Unit) {
    var selectedEspecialidad by remember { mutableStateOf("Todas") }
    val especialidades = listOf("Todas", "Cardiología", "Pediatría", "Dermatología")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Hola, Joel", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(especialidades) { esp ->
                FilterChip(
                    selected = selectedEspecialidad == esp,
                    onClick = { selectedEspecialidad = esp },
                    label = { Text(esp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Médicos disponibles", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        val medicosFiltrados = if (selectedEspecialidad == "Todas") {
            Repository.medicos
        } else {
            Repository.medicos.filter { it.especialidad == selectedEspecialidad }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(medicosFiltrados) { medico ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDoctorSelect(medico.id) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(medico.nombre, fontWeight = FontWeight.Bold)
                            Text(medico.especialidad, color = Color.Gray, fontSize = 14.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(medico.calificacion.toString(), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}