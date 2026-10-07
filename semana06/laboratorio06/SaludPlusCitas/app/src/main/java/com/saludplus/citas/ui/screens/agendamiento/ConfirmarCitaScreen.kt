package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.saludplus.citas.data.model.Cita
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmarCitaScreen(
    navController: NavController,
    medicoId: String,
    fecha: String,
    hora: String
) {
    val medico = Repositorio.obtenerMedicoPorId(medicoId)
    var motivo by remember { mutableStateOf("Consulta de rutina") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar cita", fontWeight = FontWeight.Bold) },
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        Text("${medico?.especialidadNombre ?: "Medicina General"} • CMP: ${medico?.cmp ?: "CMP-12345"}", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CitaDetalleItem(Icons.Default.DateRange, "Fecha", fecha.ifEmpty { "16-09-2026" })
            CitaDetalleItem(Icons.Default.Schedule, "Hora", hora.ifEmpty { "10:00" })
            CitaDetalleItem(Icons.Default.MedicalServices, "Tipo de atención", "Consulta presencial")
            CitaDetalleItem(Icons.Default.LocationOn, "Dirección", "Av. Los Olivos 123, Lima")

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo de consulta (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val emailUsuario = Repositorio.usuarioActual?.email ?: "usuario@ejemplo.com"
                    val nuevaCita = Cita(
                        id = UUID.randomUUID().toString(),
                        medicoId = medicoId.ifEmpty { medico?.id ?: "m2" },
                        especialidadId = medico?.especialidadId ?: "1",
                        usuarioEmail = emailUsuario,
                        fecha = fecha.ifEmpty { "16-09-2026" },
                        hora = hora.ifEmpty { "10:00" },
                        direccion = "Av. Los Olivos 123, Lima",
                        motivo = motivo
                    )
                    Repositorio.agregarCita(nuevaCita)

                    navController.navigate(Rutas.CitaExitosa.ruta)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Agendar cita", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun CitaDetalleItem(icon: ImageVector, titulo: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(titulo, color = Color.Gray, fontSize = 11.sp)
            Text(valor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}