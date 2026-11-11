package com.tecsup.lavacampus.ui.soporte

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel
import java.util.Locale

val ColorVerdeSoporte = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoporteServiciosScreen(
    viewModel: ServiciosViewModel,
    onNuevoServicio: () -> Unit,
    onEditarServicio: (Servicio) -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val servicios by viewModel.todosLosServicios.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Soporte - Servicios y Tarifas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "Panel Técnico de Servicios",
                            fontSize = 12.sp,
                            color = ColorVerdeSoporte,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoServicio,
                containerColor = ColorVerdeSoporte,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo servicio")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ColorVerdeSoporte.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = ColorVerdeSoporte,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Gestión de Tarifas de Soporte",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ColorVerdeSoporte
                        )
                        Text(
                            "Activa, desactiva o edita precios vigentes de catálogo",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(servicios) { s ->
                    TarjetaServicioSoporte(
                        servicio = s,
                        onToggleActivo = { activo ->
                            viewModel.cambiarEstadoActivo(s, activo)
                        },
                        onEditar = { onEditarServicio(s) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaServicioSoporte(
    servicio: Servicio,
    onToggleActivo: (Boolean) -> Unit,
    onEditar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    IconoServicio(
                        nombreServicio = servicio.nombre,
                        backgroundColor = ColorVerdeSoporte.copy(alpha = 0.15f),
                        tintColor = ColorVerdeSoporte
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        servicio.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = String.format(Locale.US, "S/ %.2f por %s", servicio.precio, servicio.unidad),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = servicio.activo,
                    onCheckedChange = onToggleActivo,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = ColorVerdeSoporte,
                        checkedThumbColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onEditar) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar servicio",
                        tint = ColorVerdeSoporte,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}