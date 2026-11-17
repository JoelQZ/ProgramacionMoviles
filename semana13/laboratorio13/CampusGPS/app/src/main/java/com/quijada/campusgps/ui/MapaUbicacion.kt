package com.quijada.campusgps.ui

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.quijada.campusgps.model.LugarConDistancia
import com.quijada.campusgps.model.Posicion
import org.osmdroid.config.Configuration
import org.osmdroid.library.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

@Composable
fun MapaUbicacion(
    posicionActual: Posicion?,
    lugares: List<LugarConDistancia> = emptyList(),
    seleccionado: Int? = null,
    masCercanoId: Int? = null,
    radioMetros: Int? = null,
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

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
        }
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

    LaunchedEffect(posicionActual, lugares, seleccionado, masCercanoId, radioMetros) {
        mapView.overlays.clear()

        // Dibujar círculo de radio si aplica (antes de los marcadores)
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

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}
