package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.saludplus.citas.navigation.Rutas

data class EspecialidadItem(val id: String, val nombre: String, val desc: String, val icono: ImageVector, val colorBg: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EspecialidadesScreen(
    navController: NavController,
    sedeFiltro: String = ""
) {
    var queryBusqueda by remember { mutableStateOf("") }

    val lista = listOf(
        EspecialidadItem("1", "Medicina General", "Atención médica primaria", Icons.Default.MedicalServices, Color(0xFFE0F2FE)),
        EspecialidadItem("2", "Pediatría", "Atención para bebés y niños", Icons.Default.ChildCare, Color(0xFFFEF3C7)),
        EspecialidadItem("3", "Ginecología", "Salud de la mujer", Icons.Default.Favorite, Color(0xFFFCE7F3)),
        EspecialidadItem("4", "Cardiología", "Corazón y sistema sanguíneo", Icons.Default.MonitorHeart, Color(0xFFFEE2E2)),
        EspecialidadItem("5", "Dermatología", "Piel, cabello y uñas", Icons.Default.Face, Color(0xFFFEF3C7)),
        EspecialidadItem("6", "Traumatología", "Huesos y articulaciones", Icons.Default.Build, Color(0xFFE0F2FE)),
        EspecialidadItem("7", "Oftalmología", "Salud visual", Icons.Default.Visibility, Color(0xFFE0E7FF))
    )

    val listaFiltrada = lista.filter {
        it.nombre.contains(queryBusqueda, ignoreCase = true)
    }

    val tituloBarra = if (sedeFiltro.isNotBlank()) "Especialidades en $sedeFiltro" else "Especialidades"

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = queryBusqueda,
                onValueChange = { queryBusqueda = it },
                placeholder = { Text("Buscar especialidad...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listaFiltrada) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val parametroRuta = if (sedeFiltro.isNotBlank()) {
                                    "SEDE_${sedeFiltro}_ESP_${item.id}"
                                } else {
                                    item.id
                                }
                                navController.navigate(Rutas.Medicos.crearRuta(parametroRuta))
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(item.colorBg, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(item.icono, contentDescription = null, tint = Color(0xFF2563EB))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(item.desc, color = Color.Gray, fontSize = 12.sp)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}