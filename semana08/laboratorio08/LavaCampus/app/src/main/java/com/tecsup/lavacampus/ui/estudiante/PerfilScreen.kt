package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
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
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.ui.navigation.Rutas
import com.tecsup.lavacampus.ui.theme.ColorEstudiante
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PerfilScreen(
    usuario: Usuario,
    repository: LavaRepository,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onNavegarTab: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefsStore = remember { PreferenciasStore(context) }

    val modoOscuro by prefsStore.modoOscuro.collectAsState(initial = false)
    val avisos by prefsStore.notificacionesEstudiante.collectAsState(initial = true)

    val listaPedidosConDetalle by repository.observarPedidosPorUsuario(usuario.id).collectAsState(initial = emptyList())

    val pedidosActivos = remember(listaPedidosConDetalle) {
        listaPedidosConDetalle.count {
            it.pedido.estado == "RECIBIDO" || it.pedido.estado == "EN_PROCESO" || it.pedido.estado == "LISTO"
        }
    }

    val pedidosTotales = remember(listaPedidosConDetalle) {
        listaPedidosConDetalle.size
    }

    val kilosLavados = remember(listaPedidosConDetalle) {
        listaPedidosConDetalle.flatMap { it.detalles }.sumOf { d ->
            val unidad = d.servicio.unidad.lowercase(Locale.getDefault())
            if (unidad.contains("kilo") || unidad.contains("kg")) {
                d.detalle.cantidad
            } else 0.0
        }
    }

    Scaffold(
        bottomBar = {
            BarraInferiorEstudiante(rutaSeleccionada = Rutas.PERFIL_ESTUDIANTE, onNavegar = onNavegarTab)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Perfil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp)
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
                        color = ColorEstudiante.copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = ColorEstudiante,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            usuario.nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            usuario.correo,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ColorEstudiante.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "Estudiante",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorEstudiante,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TarjetaContadorEstudiante("$pedidosActivos", "Pedidos activos", Modifier.weight(1f))
                TarjetaContadorEstudiante("$pedidosTotales", "Pedidos totales", Modifier.weight(1f))
                TarjetaContadorEstudiante(
                    String.format(Locale.US, "%.0f kg", kilosLavados),
                    "Lavados",
                    Modifier.weight(1f)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCambiarPerfil() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ColorEstudiante)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Cambiar de perfil", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text("Modo práctica: ir a vista Administrador", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
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
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Modo oscuro", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("Cambia el tema de la app", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = modoOscuro,
                            onCheckedChange = { nuevoValor ->
                                scope.launch { prefsStore.guardarModoOscuro(nuevoValor) }
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = ColorEstudiante)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Avisos de pedido listo", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("Notificar cuando puedas recoger", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = avisos,
                            onCheckedChange = { nuevoValor ->
                                scope.launch { prefsStore.guardarNotificacionesEstudiante(nuevoValor) }
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = ColorEstudiante)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text("Se guardan en el dispositivo", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("con DataStore", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorEstudiante)
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onCerrarSesion,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(22.dp)
            ) {
                Text("Cerrar sesión", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun TarjetaContadorEstudiante(
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ColorEstudiante)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = etiqueta,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun BarraInferiorEstudiante(rutaSeleccionada: String, onNavegar: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.INICIO_ESTUDIANTE,
            onClick = { onNavegar(Rutas.INICIO_ESTUDIANTE) },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Inicio", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorEstudiante,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedTextColor = ColorEstudiante,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = ColorEstudiante.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.MIS_PEDIDOS,
            onClick = { onNavegar(Rutas.MIS_PEDIDOS) },
            icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
            label = { Text("Pedidos", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorEstudiante,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedTextColor = ColorEstudiante,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = ColorEstudiante.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.TARIFAS,
            onClick = { onNavegar(Rutas.TARIFAS) },
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = null) },
            label = { Text("Tarifas", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorEstudiante,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedTextColor = ColorEstudiante,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = ColorEstudiante.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.PERFIL_ESTUDIANTE,
            onClick = { onNavegar(Rutas.PERFIL_ESTUDIANTE) },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColorEstudiante,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedTextColor = ColorEstudiante,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = ColorEstudiante.copy(alpha = 0.15f)
            )
        )
    }
}