package com.quijada.campusgps.ui

import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.quijada.campusgps.model.LugarConDistancia
import com.quijada.campusgps.model.Posicion
import com.quijada.campusgps.ui.theme.AzulPrincipal
import org.osmdroid.config.Configuration
import org.osmdroid.library.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun MapaUbicacion(
    posicionActual: Posicion?,
    lugares: List<LugarConDistancia> = emptyList(),
    seleccionado: Int? = null,
    masCercanoId: Int? = null,
    radioMetros: Int? = null,
    permisoConcedido: Boolean = false,
    modoOscuro: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Configuration.getInstance().userAgentValue = context.packageName

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(13.0)
            controller.setCenter(GeoPoint(-12.0464, -77.0428))
        }
    }

    val locationOverlay = remember(mapView) {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            locationOverlay.disableMyLocation()
            mapView.onPause()
        }
    }

    LaunchedEffect(modoOscuro) {
        if (modoOscuro) {
            mapView.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
        } else {
            mapView.overlayManager.tilesOverlay.setColorFilter(null)
        }
        mapView.invalidate()
    }

    LaunchedEffect(seleccionado, posicionActual) {
        if (seleccionado != null) {
            val lugarSeleccionado = lugares.find { it.lugar.id == seleccionado }?.lugar
            if (lugarSeleccionado != null) {
                val point = GeoPoint(lugarSeleccionado.latitud, lugarSeleccionado.longitud)
                mapView.controller.setZoom(17.5)
                mapView.controller.animateTo(point)
            }
        } else if (posicionActual != null) {
            val point = GeoPoint(posicionActual.latitud, posicionActual.longitud)
            mapView.controller.setZoom(17.5)
            mapView.controller.animateTo(point)
        }
    }

    LaunchedEffect(posicionActual, lugares, seleccionado, masCercanoId, radioMetros, permisoConcedido) {
        mapView.overlays.clear()

        // 1. Punto azul nativo mediante MyLocationNewOverlay si hay permiso
        if (permisoConcedido) {
            locationOverlay.enableMyLocation()
            mapView.overlays.add(locationOverlay)
        } else {
            locationOverlay.disableMyLocation()
        }

        // 2. Círculo de radio si aplica (antes de los marcadores)
        if (posicionActual != null && radioMetros != null) {
            val centerPoint = GeoPoint(posicionActual.latitud, posicionActual.longitud)
            val circlePoints = Polygon.pointsAsCircle(centerPoint, radioMetros.toDouble())
            val circlePolygon = Polygon(mapView).apply {
                points = circlePoints
                fillPaint.color = Color.argb(45, 25, 118, 210)
                outlinePaint.color = Color.rgb(25, 118, 210)
                outlinePaint.strokeWidth = 2f
            }
            mapView.overlays.add(circlePolygon)
        }

        val defaultIcon = ContextCompat.getDrawable(context, R.drawable.marker_default)

        var userMarkerToOpen: Marker? = null
        if (posicionActual != null) {
            val userPoint = GeoPoint(posicionActual.latitud, posicionActual.longitud)
            val userMarker = Marker(mapView).apply {
                position = userPoint
                title = "Estás aquí"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                defaultIcon?.constantState?.newDrawable()?.mutate()?.let { iconDrawable ->
                    iconDrawable.setTint(Color.RED)
                    icon = iconDrawable
                }
            }
            mapView.overlays.add(userMarker)
            userMarkerToOpen = userMarker
        }

        var selectedMarkerToOpen: Marker? = null
        lugares.forEach { item ->
            val lugar = item.lugar
            val lugarPoint = GeoPoint(lugar.latitud, lugar.longitud)
            val isMasCercano = (lugar.id == masCercanoId)
            val isSeleccionado = (lugar.id == seleccionado)

            val markerColor = if (isMasCercano) Color.parseColor("#00E676") else Color.parseColor("#1976D2")

            val marker = Marker(mapView).apply {
                position = lugarPoint
                title = lugar.nombre
                snippet = lugar.categoria
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                defaultIcon?.constantState?.newDrawable()?.mutate()?.let { iconDrawable ->
                    iconDrawable.setTint(markerColor)
                    icon = iconDrawable
                }
            }

            mapView.overlays.add(marker)

            if (isSeleccionado) {
                selectedMarkerToOpen = marker
            }
        }

        if (selectedMarkerToOpen != null) {
            selectedMarkerToOpen.showInfoWindow()
        } else {
            userMarkerToOpen?.showInfoWindow()
        }

        mapView.invalidate()
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        if (permisoConcedido || posicionActual != null) {
            SmallFloatingActionButton(
                onClick = {
                    val myLoc = locationOverlay.myLocation
                    if (myLoc != null) {
                        mapView.controller.setZoom(17.5)
                        mapView.controller.animateTo(myLoc)
                    } else if (posicionActual != null) {
                        val point = GeoPoint(posicionActual.latitud, posicionActual.longitud)
                        mapView.controller.setZoom(17.5)
                        mapView.controller.animateTo(point)
                    }
                },
                containerColor = if (modoOscuro) androidx.compose.ui.graphics.Color(0xFF1E293B) else androidx.compose.ui.graphics.Color.White,
                contentColor = if (modoOscuro) androidx.compose.ui.graphics.Color(0xFF38BDF8) else AzulPrincipal,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 290.dp, end = 16.dp)
                    .zIndex(10f)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Mi ubicación",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
