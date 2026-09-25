package com.quijada.clinicasalud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppointmentsScreen() {
    var citasState by remember { mutableStateOf(Repository.citas.toList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Mis Citas", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (citasState.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes citas programadas", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(citasState) { cita ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${cita.fecha} - ${cita.hora}",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(cita.medicoNombre, fontWeight = FontWeight.SemiBold)
                            Text(cita.especialidad, color = Color.Gray, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estado: ${cita.estado}",
                                    color = if (cita.estado == "Confirmada") Color(0xFF2E7D32) else Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                if (cita.estado == "Confirmada") {
                                    TextButton(
                                        onClick = {
                                            cita.estado = "Cancelada"
                                            citasState = Repository.citas.toList()
                                        }
                                    ) {
                                        Text("Cancelar Cita", color = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}