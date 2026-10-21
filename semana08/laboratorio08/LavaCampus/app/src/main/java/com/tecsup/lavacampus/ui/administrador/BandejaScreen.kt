    package com.tecsup.lavacampus.ui.administrador

    import androidx.compose.foundation.BorderStroke
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.isSystemInDarkTheme
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.LazyRow
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.automirrored.filled.ArrowBack
    import androidx.compose.material.icons.filled.BarChart
    import androidx.compose.material.icons.filled.ListAlt
    import androidx.compose.material.icons.filled.LocalOffer
    import androidx.compose.material.icons.filled.People
    import androidx.compose.material.icons.filled.Search
    import androidx.compose.material.icons.filled.Settings
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
    import com.tecsup.lavacampus.model.EstadoPedido
    import com.tecsup.lavacampus.ui.components.EstadoPedidoChip
    import com.tecsup.lavacampus.ui.components.IconoServicio
    import com.tecsup.lavacampus.ui.navigation.Rutas
    import com.tecsup.lavacampus.ui.theme.ColorAdministrador
    import com.tecsup.lavacampus.viewmodel.BandejaViewModel
    import java.util.Locale

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BandejaScreen(
        viewModel: BandejaViewModel,
        onVerGestionPedido: (Int) -> Unit,
        onVolver: () -> Unit = {},
        onNavegarTab: (String) -> Unit = {}
    ) {
        val pedidos by viewModel.pedidos.collectAsState()
        val textoBusqueda by viewModel.textoBusqueda.collectAsState()
        val filtroEstado by viewModel.filtroEstado.collectAsState()

        val opcionesFiltro = listOf("Todos", "Recibido", "En proceso", "Listo")

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                "Pedidos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(end = 48.dp)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onVolver) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = {
                BarraInferiorAdmin(rutaSeleccionada = Rutas.BANDEJA_PEDIDOS, onNavegar = onNavegarTab)
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { viewModel.actualizarBusqueda(it) },
                    placeholder = {
                        Text(
                            "Buscar por estudiante o número",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = ColorAdministrador,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(opcionesFiltro) { item ->
                        FilterChip(
                            selected = filtroEstado == item,
                            onClick = { viewModel.seleccionarFiltro(item) },
                            label = { Text(item, fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ColorAdministrador,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (pedidos.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "No hay pedidos registrados",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pedidos) { item ->
                            ItemPedidoAdminTarjeta(
                                item = item,
                                onClick = { onVerGestionPedido(item.pedido.id) },
                                onAccionRapida = {
                                    viewModel.avanzarEstadoRapido(item)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ItemPedidoAdminTarjeta(
        item: PedidoConDetalle,
        onClick: () -> Unit,
        onAccionRapida: () -> Unit
    ) {
        val p = item.pedido
        val estStr = p.estado.uppercase()

        val estadoEnum = try {
            EstadoPedido.valueOf(estStr)
        } catch (e: Exception) {
            EstadoPedido.RECIBIDO
        }

        val primerServicio = item.detalles.firstOrNull()?.servicio?.nombre ?: ""
        val nombreEstudiante = item.usuario?.nombre ?: "Estudiante"

        val resumenDetalle = item.detalles.joinToString(" + ") { d ->
            val unStr = if (d.servicio.unidad.equals("KG", ignoreCase = true)) "kg" else "p"
            "${d.detalle.cantidad} $unStr"
        }

        val esOscuro = isSystemInDarkTheme()

        val (fondoSemantico, colorSemantico) = when (estadoEnum) {
            EstadoPedido.RECIBIDO -> if (esOscuro) Pair(Color(0xFF451A03), Color(0xFFFDE68A)) else Pair(Color(0xFFFEF3C7), Color(0xFFD97706))
            EstadoPedido.EN_PROCESO -> if (esOscuro) Pair(Color(0xFF1E1B4B), Color(0xFFC7D2FE)) else Pair(Color(0xFFEEF2FF), Color(0xFF4F46E5))
            EstadoPedido.LISTO -> if (esOscuro) Pair(Color(0xFF064E3B), Color(0xFFA7F3D0)) else Pair(Color(0xFFDCFCE7), Color(0xFF16A34A))
            EstadoPedido.ENTREGADO -> if (esOscuro) Pair(Color(0xFF1E293B), Color(0xFF94A3B8)) else Pair(Color(0xFFF1F5F9), Color(0xFF64748B))
            EstadoPedido.CANCELADO -> if (esOscuro) Pair(Color(0xFF450A0A), Color(0xFFFCA5A5)) else Pair(Color(0xFFFEE2E2), Color(0xFFDC2626))
        }

        Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.2.dp, colorSemantico)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            IconoServicio(
                                nombreServicio = primerServicio,
                                backgroundColor = fondoSemantico,
                                tintColor = colorSemantico
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Pedido #${String.format(Locale.US, "%03d", p.id)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "$nombreEstudiante • $resumenDetalle",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        String.format(Locale.US, "S/ %.2f", p.total),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ColorAdministrador
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EstadoPedidoChip(estado = estadoEnum)

                    when (estStr) {
                        "RECIBIDO" -> {
                            Button(
                                onClick = onAccionRapida,
                                colors = ButtonDefaults.buttonColors(containerColor = ColorAdministrador),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Iniciar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        "EN_PROCESO" -> {
                            Button(
                                onClick = onClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ColorAdministrador),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Marcar listo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        "LISTO" -> {
                            Button(
                                onClick = onClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ColorAdministrador),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Entregar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
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