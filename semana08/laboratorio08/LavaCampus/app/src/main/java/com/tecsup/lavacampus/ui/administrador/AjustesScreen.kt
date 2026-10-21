package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.ui.navigation.Rutas
import com.tecsup.lavacampus.ui.theme.ColorAdministrador
import kotlinx.coroutines.launch

@Composable
fun AjustesScreen(
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onNavegarTab: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefsStore = remember { PreferenciasStore(context) }

    val modoOscuro by prefsStore.modoOscuro.collectAsState(initial = false)
    val notificacionesAdmin by prefsStore.notificacionesAdmin.collectAsState(initial = true)
    var recibirPedidos by remember { mutableStateOf(true) }

    val esOscuro = isSystemInDarkTheme()

    Scaffold(
        bottomBar = {
            BarraInferiorAdmin(rutaSeleccionada = Rutas.AJUSTES_ADMIN, onNavegar = onNavegarTab)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Ajustes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = ColorAdministrador.copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = ColorAdministrador,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            "Administración",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "admin@lavacampus.test",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ColorAdministrador.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "Perfil activo",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorAdministrador,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCambiarPerfil() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ColorAdministrador)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Cambiar de perfil", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Text("Modo práctica: ir a vista Estudiante", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Text(
                "Preferencias",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Modo oscuro",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Cambia el tema de la app",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = modoOscuro,
                            onCheckedChange = { nuevoValor ->
                                scope.launch { prefsStore.guardarModoOscuro(nuevoValor) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = ColorAdministrador,
                                checkedThumbColor = Color.White
                            )
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Notificaciones",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Avisar pedidos nuevos",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = notificacionesAdmin,
                            onCheckedChange = { nuevoValor ->
                                scope.launch { prefsStore.guardarNotificacionesAdmin(nuevoValor) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = ColorAdministrador,
                                checkedThumbColor = Color.White
                            )
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Recibir pedidos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Abrir o cerrar la recepción",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = recibirPedidos,
                            onCheckedChange = { recibirPedidos = it },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = ColorAdministrador,
                                checkedThumbColor = Color.White
                            )
                        )
                    }
                }
            }

            val esTemaOscuro = modoOscuro || esOscuro
            val fondoDataStore = if (esTemaOscuro) ColorAdministrador.copy(alpha = 0.15f) else Color(0xFFF3EDF7)
            val textoSecundarioDataStore = if (esTemaOscuro) Color.White.copy(alpha = 0.7f) else Color(0xFF6B7280)
            val textoPrincipalDataStore = if (esTemaOscuro) Color(0xFFD8B4FE) else ColorAdministrador

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = fondoDataStore)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Se guardan en el dispositivo",
                        fontSize = 11.sp,
                        color = textoSecundarioDataStore
                    )
                    Text(
                        text = "con DataStore",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textoPrincipalDataStore
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onCerrarSesion,
                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Cerrar sesión", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun BarraInferiorAdmin(rutaSeleccionada: String, onNavegar: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.PANEL_ADMIN,
            onClick = { onNavegar(Rutas.PANEL_ADMIN) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Panel", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorAdministrador,
                selectedTextColor = ColorAdministrador,
                indicatorColor = ColorAdministrador.copy(alpha = 0.15f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.BANDEJA_PEDIDOS,
            onClick = { onNavegar(Rutas.BANDEJA_PEDIDOS) },
            icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
            label = { Text("Pedidos", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorAdministrador,
                selectedTextColor = ColorAdministrador,
                indicatorColor = ColorAdministrador.copy(alpha = 0.15f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.SERVICIOS_ADMIN,
            onClick = { onNavegar(Rutas.SERVICIOS_ADMIN) },
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = null) },
            label = { Text("Servicios", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorAdministrador,
                selectedTextColor = ColorAdministrador,
                indicatorColor = ColorAdministrador.copy(alpha = 0.15f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.CLIENTES_ADMIN,
            onClick = { onNavegar(Rutas.CLIENTES_ADMIN) },
            icon = { Icon(Icons.Default.People, contentDescription = null) },
            label = { Text("Clientes", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorAdministrador,
                selectedTextColor = ColorAdministrador,
                indicatorColor = ColorAdministrador.copy(alpha = 0.15f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.AJUSTES_ADMIN,
            onClick = { onNavegar(Rutas.AJUSTES_ADMIN) },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Ajustes", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorAdministrador,
                selectedTextColor = ColorAdministrador,
                indicatorColor = ColorAdministrador.copy(alpha = 0.15f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}