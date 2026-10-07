package com.saludplus.citas.ui.screens.citas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCitasScreen(navController: NavHostController) {
    val citasUsuario = Repositorio.obtenerCitasUsuario()
    val citas = if (citasUsuario.isNotEmpty()) {
        citasUsuario
    } else {
        Repositorio.obtenerMedicoPorId("m1")?.let { Repositorio.obtenerCitasUsuario() } ?: emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Citas Médicas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (citas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No tienes citas agendadas.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(citas) { cita ->
                    val medico = Repositorio.obtenerMedicoPorId(cita.medicoId)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Rutas.DetalleCita.crearRuta(cita.id))
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = medico?.nombre ?: "Médico",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = medico?.especialidadNombre ?: "Especialidad",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "📅 ${cita.fecha} • ⏰ ${cita.hora}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Motivo: ${cita.motivo}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}