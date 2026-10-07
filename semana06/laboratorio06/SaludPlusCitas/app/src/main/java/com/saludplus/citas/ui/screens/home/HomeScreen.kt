package com.saludplus.citas.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val usuario = Repositorio.usuarioActual

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SaludPlus") },
                actions = {
                    IconButton(onClick = { navController.navigate(Rutas.Notificaciones.ruta) }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                    }
                    IconButton(onClick = {
                        Repositorio.cerrarSesion()
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "¡Hola, ${usuario?.nombre ?: "Usuario"}!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¿En qué te podemos ayudar hoy?",
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Accesos Rápidos",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TarjetaOpcionHome(
                        titulo = "Agendar Cita",
                        icono = Icons.Default.CalendarMonth,
                        onClick = { navController.navigate(Rutas.Especialidades.ruta) }
                    )
                }
                item {
                    TarjetaOpcionHome(
                        titulo = "Mis Citas",
                        icono = Icons.Default.Event,
                        onClick = { navController.navigate(Rutas.MisCitas.ruta) }
                    )
                }
                item {
                    TarjetaOpcionHome(
                        titulo = "Resultados",
                        icono = Icons.Default.Assignment,
                        onClick = { navController.navigate(Rutas.Resultados.ruta) }
                    )
                }
                item {
                    TarjetaOpcionHome(
                        titulo = "Mi Perfil",
                        icono = Icons.Default.Person,
                        onClick = { navController.navigate(Rutas.Perfil.ruta) }
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaOpcionHome(
    titulo: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = titulo,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}