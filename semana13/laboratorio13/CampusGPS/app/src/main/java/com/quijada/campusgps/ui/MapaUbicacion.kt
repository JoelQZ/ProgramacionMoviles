package com.quijada.campusgps.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.quijada.campusgps.model.LugarConDistancia
import com.quijada.campusgps.model.Posicion

@Composable
fun MapaUbicacion(
    posicionActual: Posicion?,
    lugares: List<LugarConDistancia> = emptyList(),
    seleccionado: Int? = null,
    modifier: Modifier = Modifier
) {
    val limaLatLng = LatLng(-12.0464, -77.0428)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(limaLatLng, 12f)
    }

    val targetLatLng = remember(posicionActual) {
        posicionActual?.let { LatLng(it.latitud, it.longitud) }
    }

    val markerState = remember(targetLatLng) {
        targetLatLng?.let { MarkerState(position = it) }
    }

    LaunchedEffect(targetLatLng) {
        if (targetLatLng != null) {
            markerState?.position = targetLatLng
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(targetLatLng, 16f)
            )
            markerState?.showInfoWindow()
        }
    }

    LaunchedEffect(seleccionado) {
        if (seleccionado != null) {
            val lugarSeleccionado = lugares.find { it.lugar.id == seleccionado }?.lugar
            if (lugarSeleccionado != null) {
                val target = LatLng(lugarSeleccionado.latitud, lugarSeleccionado.longitud)
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(target, 17f)
                )
            }
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            markerState?.showInfoWindow()
        }
    ) {
        if (targetLatLng != null && markerState != null) {
            Marker(
                state = markerState,
                title = "Estás aquí"
            )
        }

        lugares.forEach { item ->
            val lugar = item.lugar
            val markerStateLugar = rememberUpdatedMarkerState(
                position = LatLng(lugar.latitud, lugar.longitud)
            )
            LaunchedEffect(seleccionado) {
                if (lugar.id == seleccionado) {
                    markerStateLugar.showInfoWindow()
                }
            }
            key(lugar.id) {
                Marker(
                    state = markerStateLugar,
                    title = lugar.nombre,
                    snippet = lugar.categoria,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
        }
    }
}
