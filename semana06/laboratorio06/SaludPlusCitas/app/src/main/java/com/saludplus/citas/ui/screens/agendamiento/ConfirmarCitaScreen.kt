package com.saludplus.citas.ui.screens.agendamiento

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saludplus.citas.data.model.Cita
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.components.BotonPrincipal
import com.saludplus.citas.ui.components.TopBarSaludPlus
import java.util.UUID

@Composable
fun ConfirmarCitaScreen(
    navController: NavHostController,
    medicoId: String,
    fecha: String,
    hora: String
) {
    val medico = Repositorio.obtenerMedicoPorId(medicoId)
    val usuario = Repositorio.usuarioActual
    var direccion by remember { mutableStateOf("Av. Los Olivos 123, Lima") }
    var motivo by remember { mutableStateOf("Consulta médica de rutina") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopBarSaludPlus(
                titulo = "Confirmar Cita",
                onAtrasClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(text = "Resumen de la reserva", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Paciente: ${usuario?.nombre ?: ""}")
            Text(text = "Médico: ${medico?.nombre ?: ""}")
            Text(text = "Especialidad: ${medico?.especialidadNombre ?: ""}")
            Text(text = "Fecha: $fecha")
            Text(text = "Hora: $hora")

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección de atención") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo de consulta") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            BotonPrincipal(
                texto = "Confirmar y Reservar Cita",
                onClick = {
                    if (direccion.isBlank() || motivo.isBlank()) {
                        Toast.makeText(context, "Complete la dirección y el motivo", Toast.LENGTH_SHORT).show()
                    } else {
                        val nuevaCita = Cita(
                            id = UUID.randomUUID().toString(),
                            usuarioEmail = usuario?.email ?: "",
                            medicoId = medicoId,
                            especialidadId = medico?.especialidadId ?: "",
                            fecha = fecha,
                            hora = hora,
                            direccion = direccion,
                            motivo = motivo
                        )
                        Repositorio.agregarCita(nuevaCita)
                        navController.navigate(Rutas.CitaExitosa.ruta) {
                            popUpTo(Rutas.Home.ruta) { inclusive = false }
                        }
                    }
                }
            )
        }
    }
}