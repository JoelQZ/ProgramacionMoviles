package com.quijada.clinicasalud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDrawerScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedScreen by remember { mutableStateOf("Inicio") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("JP", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Juan Pérez", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Paciente", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = selectedScreen == "Inicio",
                    onClick = {
                        selectedScreen = "Inicio"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Mis citas") },
                    selected = selectedScreen == "Mis citas",
                    onClick = {
                        selectedScreen = "Mis citas"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Historial médico") },
                    selected = selectedScreen == "Historial médico",
                    onClick = {
                        selectedScreen = "Historial médico"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (selectedScreen == "Inicio") "Clínica Salud+" else selectedScreen) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                when (selectedScreen) {
                    "Inicio" -> HomeScreen(onDoctorSelect = {})
                    else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Sección: $selectedScreen")
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onDoctorSelect: (Int) -> Unit) {
    var selectedEspecialidad by remember { mutableStateOf("Todas") }
    val especialidades = listOf("Todas", "Cardiología", "Pediatría", "Dermatología")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Hola, Juan", fontSize = 14.sp, color = Color.Gray)
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

@Composable
fun DoctorDetailScreen(
    medicoId: Int,
    onAgendarClick: () -> Unit
) {
    val medico = Repository.medicos.find { it.id == medicoId } ?: Repository.medicos.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(medico.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("${medico.especialidad} • ${medico.experiencia}", color = Color.Gray)

        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${medico.calificacion} (120 reseñas)",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = medico.biografia,
            color = Color.DarkGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onAgendarClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Agendar cita", fontSize = 16.sp)
        }
    }
}