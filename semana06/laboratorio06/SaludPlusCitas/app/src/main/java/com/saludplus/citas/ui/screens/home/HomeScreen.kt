package com.saludplus.citas.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.theme.AzulFondoSuave
import com.saludplus.citas.ui.theme.AzulPrincipal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(AzulPrincipal, Color(0xFF1D4ED8))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Joel Quijada",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "joel.quijada@tecsup.edu.pe",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Paciente Asegurado",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    NavigationDrawerItem(
                        label = { Text("Sedes Médicas", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                        icon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = AzulPrincipal) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Rutas.Sedes.ruta)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFFF8FAFC)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationDrawerItem(
                        label = { Text("Buscar Doctor", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                        icon = { Icon(Icons.Default.PersonSearch, contentDescription = null, tint = AzulPrincipal) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Rutas.Medicos.crearRuta("todas"))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFFF8FAFC)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationDrawerItem(
                        label = { Text("Mi Agenda", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                        icon = { Icon(Icons.Default.Event, contentDescription = null, tint = AzulPrincipal) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Rutas.MisCitas.ruta)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFFF8FAFC)
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFDC2626)) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Rutas.Login.ruta) {
                                popUpTo(Rutas.Home.ruta) { inclusive = true }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFFFEF2F2)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    ) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "¡Hola, Joel!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text(text = "¿Qué deseas hacer hoy?", fontSize = 14.sp, color = Color.Gray)
                        }
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
                        navController.navigate(Rutas.Sedes.ruta)
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