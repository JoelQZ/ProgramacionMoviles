package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.ui.components.IconoCampanaNotificaciones
import com.tecsup.lavacampus.ui.components.ItemNotificacion
import com.tecsup.lavacampus.ui.graficos.GraficoBarras
import com.tecsup.lavacampus.ui.graficos.GraficoDona
import com.tecsup.lavacampus.ui.navigation.Rutas
import com.tecsup.lavacampus.ui.theme.ColorAdministrador
import com.tecsup.lavacampus.viewmodel.PanelViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelScreen(
    viewModel: PanelViewModel,
    onVerPedidosListos: () -> Unit = {},
    onNavegarTab: (String) -> Unit = {}
) {
    val pedidosHoy by viewModel.pedidosHoy.collectAsState()
    val enProceso by viewModel.enProceso.collectAsState()
    val porRecoger by viewModel.porRecoger.collectAsState()
    val pedidosRecibidos by viewModel.pedidosRecibidos.collectAsState()
    val ingresosSemana by viewModel.ingresosSemana.collectAsState()
    val totalPedidosSemana by viewModel.totalPedidosSemana.collectAsState()
    val porEstado by viewModel.porEstado.collectAsState()
    val porServicio by viewModel.porServicio.collectAsState()

    val esOscuro = isSystemInDarkTheme()

    val notificacionesNuevos = remember(pedidosRecibidos) {
        pedidosRecibidos.map { item ->
            ItemNotificacion(
                id = item.pedido.id.toLong(),
                titulo = "Nuevo pedido #${String.format(Locale.US, "%03d", item.pedido.id)}",
                detalle = "${item.usuario?.nombre ?: "Estudiante"} • ${item.detalles.joinToString(" + ") { "${it.detalle.cantidad} ${it.servicio.unidad}" }}",
                subtexto = String.format(Locale.US, "Total: S/ %.2f • Pendiente de iniciar", item.pedido.total)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Panel de la lavandería",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "Hoy",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconoCampanaNotificaciones(
                        tieneNotificaciones = notificacionesNuevos.isNotEmpty(),
                        listaNotificaciones = notificacionesNuevos,
                        tituloDialogo = "Nuevos pedidos por iniciar",
                        tintIcono = MaterialTheme.colorScheme.onBackground,
                        colorBrand = ColorAdministrador
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            BarraInferiorAdministrador(
                rutaSeleccionada = Rutas.PANEL_ADMIN,
                onNavegar = onNavegarTab
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                val fondoPurple = if (esOscuro) Color(0xFF3B0764) else Color(0xFFF3E8FF)
                val textoPurple = if (esOscuro) Color(0xFFE9D5FF) else Color(0xFF7E22CE)

                val fondoBlue = if (esOscuro) Color(0xFF075985) else Color(0xFFE0F2FE)
                val textoBlue = if (esOscuro) Color(0xFFBAE6FD) else Color(0xFF0369A1)

                val fondoGreen = if (esOscuro) Color(0xFF14532D) else Color(0xFFDCFCE7)
                val textoGreen = if (esOscuro) Color(0xFFA7F3D0) else Color(0xFF15803D)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TarjetaContador("Pedidos hoy", pedidosHoy, fondoPurple, textoPurple, Modifier.weight(1f))
                    TarjetaContador("En proceso", enProceso, fondoBlue, textoBlue, Modifier.weight(1f))
                    TarjetaContador("Por recoger", porRecoger, fondoGreen, textoGreen, Modifier.weight(1f))
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorAdministrador)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Ingresos de la semana", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                String.format(Locale.US, "S/ %.2f", ingresosSemana),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }
                        Text("$totalPedidosSemana pedidos", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Pedidos por estado",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        GraficoDona(datos = porEstado)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Ingresos por servicio (S/)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        GraficoBarras(datos = porServicio)
                    }
                }
            }

            if (porRecoger > 0) {
                item {
                    val fondoRecojo = if (esOscuro) Color(0xFF14532D) else Color(0xFFDCFCE7)
                    val textoRecojo1 = if (esOscuro) Color(0xFFA7F3D0) else Color(0xFF15803D)
                    val textoRecojo2 = if (esOscuro) Color(0xFF86EFAC) else Color(0xFF166534)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = fondoRecojo)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "$porRecoger pedidos esperan recojo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = textoRecojo1
                                )
                                Text("Avisos a los estudiantes", fontSize = 11.sp, color = textoRecojo2)
                            }
                            Button(
                                onClick = onVerPedidosListos,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Ver", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TarjetaContador(
    titulo: String,
    valor: Int,
    fondoColor: Color,
    textoColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = fondoColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$valor", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = textoColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(titulo, fontSize = 11.sp, color = textoColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun BarraInferiorAdministrador(
    rutaSeleccionada: String,
    onNavegar: (String) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.PANEL_ADMIN,
            onClick = { onNavegar(Rutas.PANEL_ADMIN) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Panel") },
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
            icon = { Icon(Icons.Default.ListAlt, contentDescription = "Pedidos") },
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
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = "Servicios") },
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
            icon = { Icon(Icons.Default.People, contentDescription = "Clientes") },
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
            icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
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