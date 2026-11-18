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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    val seguimientoActivo by vm.seguimientoActivo.collectAsState()

    val systemDark = isSystemInDarkTheme()
    var modoOscuro by remember { mutableStateOf(systemDark) }

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

    val solicitarSeguimiento = {
        if (tienePermisoUbicacion(context)) {
            vm.alternarSeguimientoEnVivo()
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

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        @Suppress("UNUSED_VARIABLE")
        val unusedPadding = innerPadding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (modoOscuro) Color(0xFF020617) else Color(0xFFF8FAFC))
        ) {
            // 1. Mapa en el fondo
            MapaUbicacion(
                posicionActual = (estado as? UbicacionUi.Lista)?.posicion,
                lugares = lugares,
                seleccionado = seleccionado,
                masCercanoId = masCercano?.id,
                radioMetros = radioSeleccionado,
                permisoConcedido = tienePermisoUbicacion(context),
                modoOscuro = modoOscuro,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 270.dp)
            )

            // 2. Cabecera superior adaptativa según modoOscuro
            val headerBgGradient = if (modoOscuro) {
                Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
            } else {
                Brush.horizontalGradient(listOf(Color(0xFF1976D2), Color(0xFF1565C0)))
            }

            Surface(
                color = if (modoOscuro) Color(0xFF0F172A) else AzulPrincipal,
                shape = RectangleShape,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(2f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerBgGradient)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Izquierda: Título CampusGPS
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (modoOscuro) Color(0xFF38BDF8).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (modoOscuro) Color(0xFF38BDF8) else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CampusGPS",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (modoOscuro) Color(0xFFF8FAFC) else Color.White,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Derecha: Botón de tema y píldora de coordenadas
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(
                                onClick = { modoOscuro = !modoOscuro },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (modoOscuro) Color(0xFF1E293B) else Color.White.copy(alpha = 0.18f))
                            ) {
                                Icon(
                                    imageVector = if (modoOscuro) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Alternar Tema",
                                    tint = if (modoOscuro) Color(0xFF38BDF8) else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            val listaEstado = estado as? UbicacionUi.Lista
                            Surface(
                                color = if (modoOscuro) Color(0xFF1E293B) else Color.White.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(50),
                                border = if (modoOscuro) BorderStroke(1.dp, Color(0xFF334155)) else null
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
                                    color = if (modoOscuro) Color(0xFF38BDF8) else Color.White,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Panel inferior adaptativo según modoOscuro
            val panelBgGradient = if (modoOscuro) {
                Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
            } else {
                Brush.verticalGradient(listOf(Color(0xFF1976D2), Color(0xFF1565C0)))
            }

            Surface(
                color = if (modoOscuro) Color(0xFF0F172A) else AzulPrincipal,
                shape = RectangleShape,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.55f)
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(panelBgGradient)
                        .navigationBarsPadding()
                        .padding(top = 10.dp, bottom = 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    // Fila de 3 botones principales
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = solicitarUbicacion,
                            enabled = estado !is UbicacionUi.Buscando,
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (modoOscuro) Color(0xFF38BDF8) else Color.White,
                                contentColor = if (modoOscuro) Color(0xFF0F172A) else AzulPrincipal,
                                disabledContainerColor = if (modoOscuro) Color(0xFF38BDF8).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.6f),
                                disabledContentColor = if (modoOscuro) Color(0xFF0F172A).copy(alpha = 0.5f) else AzulPrincipal.copy(alpha = 0.6f)
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Ubicación",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        OutlinedButton(
                            onClick = { masCercano?.let { vm.seleccionar(it.id) } },
                            enabled = masCercano != null,
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (modoOscuro) Color(0xFF1E293B) else Color.White.copy(alpha = 0.15f),
                                contentColor = if (modoOscuro) Color(0xFFF8FAFC) else Color.White,
                                disabledContainerColor = if (modoOscuro) Color(0xFF1E293B).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f),
                                disabledContentColor = if (modoOscuro) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.4f)
                            ),
                            border = BorderStroke(1.dp, if (modoOscuro) Color(0xFF334155) else Color.White.copy(alpha = 0.5f)),
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1.1f)
                                .height(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Más cercano",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Button(
                            onClick = solicitarSeguimiento,
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (seguimientoActivo) {
                                    if (modoOscuro) Color(0xFF10B981) else Color(0xFF00E676)
                                } else {
                                    if (modoOscuro) Color(0xFF1E293B) else Color.White.copy(alpha = 0.18f)
                                },
                                contentColor = if (seguimientoActivo) {
                                    if (modoOscuro) Color(0xFF0F172A) else Color(0xFF1B5E20)
                                } else {
                                    if (modoOscuro) Color(0xFFF8FAFC) else Color.White
                                }
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Icon(
                                imageVector = if (seguimientoActivo) Icons.Default.GpsFixed else Icons.Default.Sensors,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (seguimientoActivo) "En vivo · ON" else "En vivo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Fila horizontal de FilterChips de Categoría
                    val categorias = listOf("Todos", "Estudio", "Comida", "Eventos", "Servicios")
                    val categoriaActual = categoriaSeleccionada ?: "Todos"

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
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
                                        fontSize = 11.sp,
                                        fontWeight = if (esActiva) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (modoOscuro) Color(0xFF1E293B) else Color.White.copy(alpha = 0.15f),
                                    labelColor = if (modoOscuro) Color(0xFF94A3B8) else Color.White,
                                    selectedContainerColor = if (modoOscuro) Color(0xFF38BDF8) else Color.White,
                                    selectedLabelColor = if (modoOscuro) Color(0xFF0F172A) else AzulPrincipal
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (modoOscuro) Color(0xFF334155) else Color.White.copy(alpha = 0.4f),
                                    selectedBorderColor = if (modoOscuro) Color(0xFF38BDF8) else Color.White,
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
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
                                    containerColor = if (modoOscuro) Color(0xFF1E293B) else Color.White.copy(alpha = 0.12f),
                                    labelColor = if (modoOscuro) Color(0xFF94A3B8) else Color.White,
                                    selectedContainerColor = if (modoOscuro) Color(0xFF38BDF8) else Color.White,
                                    selectedLabelColor = if (modoOscuro) Color(0xFF0F172A) else AzulPrincipal
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (modoOscuro) Color(0xFF334155) else Color.White.copy(alpha = 0.35f),
                                    selectedBorderColor = if (modoOscuro) Color(0xFF38BDF8) else Color.White,
                                    enabled = true,
                                    selected = esActivo
                                ),
                                shape = CircleShape
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Estado informativo sobre fondo adaptativo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        when (val actual = estado) {
                            is UbicacionUi.Inicial -> {
                                Text(
                                    text = "Pulsa el botón para empezar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (modoOscuro) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            is UbicacionUi.Buscando -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = if (modoOscuro) Color(0xFF38BDF8) else Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Buscando ubicación...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (modoOscuro) Color(0xFFF8FAFC) else Color.White
                                    )
                                }
                            }
                            is UbicacionUi.Lista -> {
                                val masCercanoConDistancia = lugares.firstOrNull { it.lugar.id == masCercano?.id }
                                if (masCercano != null && masCercanoConDistancia?.metros != null) {
                                    Surface(
                                        color = if (modoOscuro) Color(0xFF1E293B) else Color.White.copy(alpha = 0.14f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = if (modoOscuro) BorderStroke(1.dp, Color(0xFF334155)) else null,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Más cercano: ${masCercano?.nombre} (${formatearDistancia(masCercanoConDistancia.metros)})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (modoOscuro) Color(0xFF38BDF8) else Color.White,
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
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Lista de lugares adaptada sobre el panel azul con parámetro modoOscuro
                    ListaLugares(
                        lugares = lugares,
                        seleccionado = seleccionado,
                        masCercanoId = masCercano?.id,
                        ultimoLugarGuardadoId = ultimoLugarGuardadoId,
                        modoOscuro = modoOscuro,
                        onClick = { vm.seleccionar(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
