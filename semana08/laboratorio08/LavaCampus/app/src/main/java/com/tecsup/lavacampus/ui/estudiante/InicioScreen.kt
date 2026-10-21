package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.IconoCampanaNotificaciones
import com.tecsup.lavacampus.ui.components.ItemNotificacion
import com.tecsup.lavacampus.ui.navigation.Rutas
import com.tecsup.lavacampus.ui.theme.ColorEstudiante
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    usuario: Usuario,
    viewModel: InicioViewModel,
    onNuevoPedido: () -> Unit,
    onVerMisPedidos: () -> Unit,
    onVerTarifas: () -> Unit,
    onVerDetallePedido: (Long) -> Unit,
    onNavegarTab: (String) -> Unit = {}
) {
    val pedidoActivo by viewModel.pedidoActivo.collectAsState()
    val recientes by viewModel.recientes.collectAsState()

    val pedidosListos = remember(pedidoActivo, recientes) {
        val lista = mutableListOf<PedidoConDetalle>()
        if (pedidoActivo?.pedido?.estado?.uppercase() == "LISTO") {
            lista.add(pedidoActivo!!)
        }
        recientes.forEach { p ->
            if (p.pedido.estado.uppercase() == "LISTO" && lista.none { it.pedido.id == p.pedido.id }) {
                lista.add(p)
            }
        }
        lista
    }

    val notificacionesListos = remember(pedidosListos) {
        pedidosListos.map { item ->
            ItemNotificacion(
                id = item.pedido.id.toLong(),
                titulo = "Pedido #${String.format(Locale.US, "%03d", item.pedido.id)} ¡Listo para recoger!",
                detalle = if (!item.pedido.casillero.isNullOrBlank()) {
                    "Recógelo en el casillero ${item.pedido.casillero}"
                } else {
                    "Casillero pendiente de asignación"
                },
                subtexto = String.format(Locale.US, "Total: S/ %.2f", item.pedido.total),
                onClickItem = { onVerDetallePedido(item.pedido.id.toLong()) }
            )
        }
    }

    Scaffold(
        bottomBar = {
            BarraInferiorEstudiante(
                rutaSeleccionada = Rutas.INICIO_ESTUDIANTE,
                onNavegar = { ruta ->
                    when (ruta) {
                        Rutas.MIS_PEDIDOS -> onVerMisPedidos()
                        Rutas.TARIFAS -> onVerTarifas()
                        else -> onNavegarTab(ruta)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hola, ${usuario.nombre.split(" ").firstOrNull() ?: usuario.nombre}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${usuario.codigo} - Tecsup",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconoCampanaNotificaciones(
                        tieneNotificaciones = notificacionesListos.isNotEmpty(),
                        listaNotificaciones = notificacionesListos,
                        tituloDialogo = "Pedidos listos para recojo",
                        tintIcono = MaterialTheme.colorScheme.onBackground,
                        colorBrand = ColorEstudiante
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = pedidoActivo != null) {
                            pedidoActivo?.let { onVerDetallePedido(it.pedido.id.toLong()) }
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorEstudiante)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (pedidoActivo != null) {
                            val p = pedidoActivo!!
                            val estadoEnum = try {
                                EstadoPedido.valueOf(p.pedido.estado.uppercase())
                            } catch (e: Exception) {
                                EstadoPedido.RECIBIDO
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tu pedido activo", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                Text("#${p.pedido.id.toString().padStart(3, '0')}", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val tituloEstado = when (estadoEnum) {
                                EstadoPedido.RECIBIDO -> "Recibido"
                                EstadoPedido.EN_PROCESO -> "En proceso"
                                EstadoPedido.LISTO -> "¡Listo para recoger!"
                                else -> p.pedido.estado
                            }

                            Text(tituloEstado, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                            if (estadoEnum == EstadoPedido.LISTO && !p.pedido.casillero.isNullOrEmpty()) {
                                Text(
                                    text = "Recógelo en el casillero ${p.pedido.casillero}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            } else {
                                Text(
                                    text = "Entrega estimada: ${p.pedido.fechaEntrega}",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            StepperEstadoLocal(estadoActual = estadoEnum)
                        } else {
                            Text(
                                text = "No tienes pedidos activos",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { onNuevoPedido() },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorEstudiante),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Nuevo pedido", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onVerMisPedidos() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ListAlt, contentDescription = null, tint = ColorEstudiante)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Mis pedidos", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Ver estado", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onVerTarifas() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = ColorEstudiante)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Tarifas", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Precios por kg", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Últimos pedidos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onVerMisPedidos) {
                        Text("Ver todos", color = ColorEstudiante)
                    }
                }
            }

            items(recientes) { pedido ->
                PedidoCardLocal(
                    pedidoConDetalle = pedido,
                    onClick = { onVerDetallePedido(pedido.pedido.id.toLong()) }
                )
            }
        }
    }
}

@Composable
private fun StepperEstadoLocal(estadoActual: EstadoPedido) {
    val pasos = listOf(
        EstadoPedido.RECIBIDO to "Recibido",
        EstadoPedido.EN_PROCESO to "En proceso",
        EstadoPedido.LISTO to "Listo",
        EstadoPedido.ENTREGADO to "Entregado"
    )

    val ordenActual = estadoActual.ordinal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(30.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        pasos.forEachIndexed { index, (_, label) ->
            val completado = index <= ordenActual
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (completado) ColorEstudiante else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (completado) {
                        Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = label,
                    fontSize = 9.sp,
                    color = if (completado) ColorEstudiante else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (completado) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun PedidoCardLocal(
    pedidoConDetalle: PedidoConDetalle,
    onClick: () -> Unit
) {
    val p = pedidoConDetalle.pedido
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Pedido #${p.id.toString().padStart(3, '0')}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val fechaTexto = remember(p.fechaCreacion) {
                    try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        sdf.format(Date(p.fechaCreacion))
                    } catch (e: Exception) {
                        p.fechaCreacion.toString()
                    }
                }

                Text(
                    text = fechaTexto,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = p.estado,
                        color = Color(0xFF2E7D32),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.US, "S/ %.2f", p.total),
                    fontWeight = FontWeight.Bold,
                    color = ColorEstudiante,
                    fontSize = 16.sp
                )
                if (!p.casillero.isNullOrEmpty()) {
                    Text("Casillero ${p.casillero}", fontSize = 11.sp, color = ColorEstudiante, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun BarraInferiorEstudiante(
    rutaSeleccionada: String,
    onNavegar: (String) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = rutaSeleccionada == Rutas.INICIO_ESTUDIANTE,
            onClick = { onNavegar(Rutas.INICIO_ESTUDIANTE) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 11.sp) },
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
            icon = { Icon(Icons.Default.ListAlt, contentDescription = "Pedidos") },
            label = { Text("Pedidos", fontSize = 11.sp) },
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
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = "Tarifas") },
            label = { Text("Tarifas", fontSize = 11.sp) },
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
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
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