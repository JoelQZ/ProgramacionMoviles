package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.components.TopBarSaludPlus

@Composable
fun MedicosScreen(navController: NavHostController, especialidadId: String) {
    val medicosFiltrados = Repositorio.medicos.filter { it.especialidadId == especialidadId }

    Scaffold(
        topBar = {
            TopBarSaludPlus(
                titulo = "Médicos Disponibles",
                onAtrasClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(medicosFiltrados) { medico ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(Rutas.FechaHora.crearRuta(medico.id))
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = medico.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Especialidad: ${medico.especialidadNombre}", fontSize = 14.sp)
                        Text(text = "CMP: ${medico.cmp}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        Text(text = "⭐ ${medico.calificacion} (${medico.resenas} reseñas)", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}