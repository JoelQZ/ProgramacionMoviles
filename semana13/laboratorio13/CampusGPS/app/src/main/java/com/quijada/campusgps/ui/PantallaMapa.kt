package com.quijada.campusgps.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.quijada.campusgps.model.TipoError
import com.quijada.campusgps.model.UbicacionUi
import com.quijada.campusgps.ui.theme.AzulPrincipal
import com.quijada.campusgps.util.formatearDistancia
import com.quijada.campusgps.viewmodel.MapaViewModel
import java.util.Locale

private fun tienePermisoUbicacion(context: Context): Boolean {
    val fineGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return fineGranted || coarseGranted
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val estado by vm.estado.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val masCercano by vm.masCercano.collectAsState()
    val categoriaSeleccionada by vm.categoriaSeleccionada.collectAsState()
    val radioSeleccionado by vm.radioSeleccionado.collectAsState()
    val ultimoLugarGuardadoId by vm.ultimoLugarGuardadoId.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            vm.buscar()
        } else {
            vm.permisoDenegado()
        }
    }

    val solicitarUbicacion = {
        if (tienePermisoUbicacion(context)) {
            vm.buscar()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (tienePermisoUbicacion(context)) {
                    val actual = estado
                    if (actual is UbicacionUi.Error && actual.tipo == TipoError.SIN_PERMISO) {
                        vm.buscar()
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            // Cabecera superior fija azul moderno
            Surface(
                color = AzulPrincipal,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(2f)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1976D2), Color(0xFF1565C0))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "CampusGPS",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    val listaEstado = estado as? UbicacionUi.Lista
                    Surface(
                        color = Color.White.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(50)
                    ) {
                        val textoCoordenadas = if (listaEstado != null) {
                            val latStr = String.format(Locale.US, "%.4f", listaEstado.posicion.latitud)
                            val lonStr = String.format(Locale.US, "%.4f", listaEstado.posicion.longitud)
                            "Lat: $latStr · Lon: $lonStr"
                        } else {
                            "Sin ubicación"
                        }

                        Text(
                            text = textoCoordenadas,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Contenedor del Mapa en la parte superior (0.45 de la pantalla) recortado en bordes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.45f)
                    .clipToBounds()
            ) {
                MapaUbicacion(
                    posicionActual = (estado as? UbicacionUi.Lista)?.posicion,
                    lugares = lugares,
                    seleccionado = seleccionado,
                    masCercanoId = masCercano?.id,
                    radioMetros = radioSeleccionado,
                    permisoConcedido = tienePermisoUbicacion(context),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Panel inferior azul vibrante estilo Bottom Sheet (0.55 de la pantalla)
            Surface(
                color = AzulPrincipal,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.55f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp, start = 16.dp, end = 16.dp, bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Barra indicadora centrada arriba
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.4f))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fila de botones principales
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = solicitarUbicacion,
                            enabled = estado !is UbicacionUi.Buscando,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = AzulPrincipal,
                                disabledContainerColor = Color.White.copy(alpha = 0.6f),
                                disabledContentColor = AzulPrincipal.copy(alpha = 0.6f)
                            ),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Obtener mi ubicación", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { masCercano?.let { vm.seleccionar(it.id) } },
                            enabled = masCercano != null,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White,
                                disabledContainerColor = Color.White.copy(alpha = 0.05f),
                                disabledContentColor = Color.White.copy(alpha = 0.4f)
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ir al más cercano", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Fila horizontal de FilterChips de Categoría
                    val categorias = listOf("Todos", "Estudio", "Comida", "Eventos", "Servicios")
                    val categoriaActual = categoriaSeleccionada ?: "Todos"

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        items(categorias) { cat ->
                            val esActiva = (cat == categoriaActual)
                            FilterChip(
                                selected = esActiva,
                                onClick = { vm.seleccionarCategoria(cat) },
                                label = {
                                    Text(
                                        text = cat,
                                        fontSize = 12.sp,
                                        fontWeight = if (esActiva) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.White.copy(alpha = 0.15f),
                                    labelColor = Color.White,
                                    selectedContainerColor = Color.White,
                                    selectedLabelColor = AzulPrincipal
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Color.White.copy(alpha = 0.4f),
                                    selectedBorderColor = Color.White,
                                    enabled = true,
                                    selected = esActiva
                                ),
                                shape = CircleShape
                            )
                        }
                    }

                    // Selector de Radio de Cercanía
                    val radios = listOf("Sin radio" to null, "100 m" to 100, "200 m" to 200, "500 m" to 500)
                    val radioActual = radioSeleccionado

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        items(radios) { (label, radioVal) ->
                            val esActivo = (radioActual == radioVal)
                            FilterChip(
                                selected = esActivo,
                                onClick = { vm.seleccionarRadio(radioVal) },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (esActivo) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.White.copy(alpha = 0.12f),
                                    labelColor = Color.White,
                                    selectedContainerColor = Color.White,
                                    selectedLabelColor = AzulPrincipal
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Color.White.copy(alpha = 0.35f),
                                    selectedBorderColor = Color.White,
                                    enabled = true,
                                    selected = esActivo
                                ),
                                shape = CircleShape
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Estado informativo sobre fondo azul
                    when (val actual = estado) {
                        is UbicacionUi.Inicial -> {
                            Text(
                                text = "Pulsa el botón para empezar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        is UbicacionUi.Buscando -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Buscando ubicación...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                        is UbicacionUi.Lista -> {
                            val masCercanoConDistancia = lugares.firstOrNull { it.lugar.id == masCercano?.id }
                            if (masCercano != null && masCercanoConDistancia?.metros != null) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Más cercano: ${masCercano?.nombre} (${formatearDistancia(masCercanoConDistancia.metros)})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                        is UbicacionUi.Error -> {
                            MensajeError(
                                error = actual,
                                onReintentar = solicitarUbicacion,
                                onAbrirAjustes = {
                                    val intent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null)
                                    )
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Lista de lugares adaptada sobre el panel azul
                    ListaLugares(
                        lugares = lugares,
                        seleccionado = seleccionado,
                        masCercanoId = masCercano?.id,
                        ultimoLugarGuardadoId = ultimoLugarGuardadoId,
                        onClick = { vm.seleccionar(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }
        }
    }
}
