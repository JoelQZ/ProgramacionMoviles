package com.saludplus.citas.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.theme.AzulFondoSuave
import com.saludplus.citas.ui.theme.AzulPrincipal

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Rutas.MisCitas.ruta) },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Citas") },
                    label = { Text("Citas") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Rutas.Resultados.ruta) },
                    icon = { Icon(Icons.Default.Description, contentDescription = "Resultados") },
                    label = { Text("Resultados") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Rutas.Perfil.ruta) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "¡Hola, Joel!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(text = "¿Qué deseas hacer hoy?", fontSize = 14.sp, color = Color.Gray)
                }
                IconButton(onClick = { navController.navigate(Rutas.Notificaciones.ruta) }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TarjetonOpcion(
                    titulo = "Agendar cita",
                    icono = Icons.Default.DateRange,
                    colorFondo = Color(0xFFE8F0FE),
                    colorIcono = AzulPrincipal,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate(Rutas.Especialidades.ruta)
                }
                TarjetonOpcion(
                    titulo = "Mis citas",
                    icono = Icons.Default.EventAvailable,
                    colorFondo = Color(0xFFE6F4EA),
                    colorIcono = Color(0xFF137333),
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate(Rutas.MisCitas.ruta)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TarjetonOpcion(
                    titulo = "Mis datos",
                    icono = Icons.Default.Person,
                    colorFondo = Color(0xFFFFEFE7),
                    colorIcono = Color(0xFFD93025),
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate(Rutas.Perfil.ruta)
                }
                TarjetonOpcion(
                    titulo = "Resultados",
                    icono = Icons.Default.Article,
                    colorFondo = Color(0xFFFEF7E0),
                    colorIcono = Color(0xFFB06000),
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate(Rutas.Resultados.ruta)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Especialidades destacadas", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Ver todas",
                    color = AzulPrincipal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate(Rutas.Especialidades.ruta) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                EspecialidadDestacadaItem(nombre = "Medicina\nGeneral", icono = Icons.Default.MedicalServices) {
                    navController.navigate(Rutas.Especialidades.ruta)
                }
                EspecialidadDestacadaItem(nombre = "Pediatría", icono = Icons.Default.Face) {
                    navController.navigate(Rutas.Especialidades.ruta)
                }
                EspecialidadDestacadaItem(nombre = "Ginecología", icono = Icons.Default.Favorite) {
                    navController.navigate(Rutas.Especialidades.ruta)
                }
            }
        }
    }
}

@Composable
fun TarjetonOpcion(
    titulo: String,
    icono: ImageVector,
    colorFondo: Color,
    colorIcono: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = titulo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EspecialidadDestacadaItem(nombre: String, icono: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(AzulFondoSuave, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = nombre, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}