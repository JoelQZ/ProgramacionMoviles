package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.navigation.Rutas
import com.tecsup.lavacampus.ui.theme.ColorEstudiante
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel
import java.util.Locale

fun Servicio.textoPrecio(): String {
    val un = this.unidad.uppercase()
    val desUnidad = when (un) {
        "KG" -> "por kg"
        "PRENDA", "UND", "UNIDAD" -> "por prenda"
        else -> "por ${this.unidad.lowercase()}"
    }
    return String.format(Locale.US, "S/ %.2f %s", this.precio, desUnidad)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarifasScreen(
    viewModel: ServiciosViewModel,
    onVolver: () -> Unit = {},
    onNavegarTab: (String) -> Unit = {}
) {
    val servicios by viewModel.serviciosActivos.collectAsState()

    val serviciosOrdenados = servicios.sortedBy { servicio ->
        when {
            servicio.nombre.contains("Lavado y secado", ignoreCase = true) -> 1
            servicio.nombre.contains("Planchado", ignoreCase = true) -> 2
            servicio.nombre.contains("Seco", ignoreCase = true) -> 3
            else -> 4
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Tarifas y servicios",
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
            BarraInferiorEstudiante(
                rutaSeleccionada = Rutas.TARIFAS,
                onNavegar = onNavegarTab
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(serviciosOrdenados) { servicio ->
                ItemTarifaTarjeta(servicio = servicio)
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = ColorEstudiante,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Horario de atención",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = ColorEstudiante
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Lunes a sábado · 8:00 a 18:00",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Recojo en casilleros del pabellón A",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                val esOscuro = isSystemInDarkTheme()
                val fondoAviso = if (esOscuro) Color(0xFF451A03) else Color(0xFFFEF3C7)
                val textoAviso = if (esOscuro) Color(0xFFFDE68A) else Color(0xFFB45309)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = fondoAviso)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "Mínimo 1 kg por servicio de lavado.",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = textoAviso
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Máximo 3 pedidos activos por estudiante.",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = textoAviso
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemTarifaTarjeta(servicio: Servicio) {
    val tiempoEntrega = if (servicio.nombre.contains("Planchado", ignoreCase = true) || servicio.nombre.contains("Seco", ignoreCase = true)) "48 h" else "24 h"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                IconoServicio(
                    nombreServicio = servicio.nombre,
                    backgroundColor = ColorEstudiante.copy(alpha = 0.15f),
                    tintColor = ColorEstudiante
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    servicio.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    servicio.textoPrecio(),
                    fontWeight = FontWeight.Bold,
                    color = ColorEstudiante,
                    fontSize = 13.sp
                )
                Text(
                    "Entrega en $tiempoEntrega",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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