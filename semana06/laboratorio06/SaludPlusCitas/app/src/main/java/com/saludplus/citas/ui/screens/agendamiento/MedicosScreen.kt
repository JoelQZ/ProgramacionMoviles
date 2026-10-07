package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicosScreen(
    navController: NavController,
    especialidadId: String
) {
    val medicosDelRepo = Repositorio.medicos.filter {
        it.especialidadId == especialidadId || especialidadId.isBlank()
    }

    val medicosAMostrar = if (medicosDelRepo.isNotEmpty()) {
        medicosDelRepo
    } else {
        Repositorio.medicos
    }

    val tituloBarra = if (especialidadId.isNotBlank()) "Médicos Disponibles" else "Médicos Disponibles"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloBarra, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (medicosAMostrar.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay médicos disponibles por el momento", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medicosAMostrar) { medico ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Rutas.FechaHora.crearRuta(medico.id))
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color(0xFFE2E8F0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = medico.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${medico.especialidadNombre} • CMP: ${medico.cmp}",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = " ${medico.calificacion} (${medico.resenas})",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Disponible hoy",
                                    color = Color(0xFF166534),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}