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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
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
import com.saludplus.citas.ui.theme.AzulPrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicosScreen(
    navController: NavController,
    especialidadId: String
) {
    val medicosAMostrar = Repositorio.medicos.filter { medico ->
        if (especialidadId.startsWith("SEDE_") && especialidadId.contains("_ESP_")) {
            val partes = especialidadId.split("_ESP_")
            val sedeNombre = partes[0].removePrefix("SEDE_")
            val espId = partes[1]
            medico.sede.contains(sedeNombre, ignoreCase = true) && medico.especialidadId == espId
        } else if (especialidadId.startsWith("SEDE_")) {
            val sedeNombre = especialidadId.removePrefix("SEDE_")
            medico.sede.contains(sedeNombre, ignoreCase = true)
        } else if (especialidadId.isNotBlank() && especialidadId != "todas") {
            medico.especialidadId == especialidadId
        } else {
            true
        }
    }

    val tituloBarra = if (especialidadId.startsWith("SEDE_") && especialidadId.contains("_ESP_")) {
        val partes = especialidadId.split("_ESP_")
        "Médicos de ${partes[0].removePrefix("SEDE_")}"
    } else if (especialidadId.startsWith("SEDE_")) {
        "Médicos de ${especialidadId.removePrefix("SEDE_")}"
    } else {
        "Médicos Disponibles"
    }

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
                Text("No hay médicos disponibles para esta selección", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(medicosAMostrar) { medico ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Rutas.FechaHora.crearRuta(medico.id))
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color(0xFFE2E8F0), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = AzulPrincipal,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = medico.nombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = Color(0xFF1E293B)
                                    )

                                    Text(
                                        text = "Especialidad: ${medico.especialidadNombre}",
                                        color = AzulPrincipal,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )

                                    Text(
                                        text = "Código: ${medico.cmp}",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = " ${medico.calificacion}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color(0xFFF1F5F9)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Sede: ${medico.sede}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF475569)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Teléfono: ${medico.telefono}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF475569)
                                )
                            }

                            Button(
                                onClick = {
                                    navController.navigate(Rutas.FechaHora.crearRuta(medico.id))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
                            ) {
                                Text("Citar", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}