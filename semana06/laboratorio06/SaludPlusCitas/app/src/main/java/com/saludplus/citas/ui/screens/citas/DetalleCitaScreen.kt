package com.saludplus.citas.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.TopBarSaludPlus

@Composable
fun DetalleCitaScreen(navController: NavHostController, citaId: String) {
    val cita = Repositorio.obtenerCitasUsuario().find { it.id == citaId }
    val medico = cita?.let { Repositorio.obtenerMedicoPorId(it.medicoId) }

    Scaffold(
        topBar = {
            TopBarSaludPlus(
                titulo = "Detalle de la Cita",
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
            if (cita != null && medico != null) {
                Text(text = medico.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(text = medico.especialidadNombre, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))

                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Fecha: ${cita.fecha}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Hora: ${cita.hora}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Dirección: ${cita.direccion}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Motivo: ${cita.motivo}", fontSize = 16.sp)
            } else {
                Text(text = "No se encontró la información de la cita.")
            }
        }
    }
}