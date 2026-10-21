package com.tecsup.lavacampus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.LineaPedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class NuevoPedidoViewModel(
    private val repository: LavaRepository,
    private val usuario: Usuario
) : ViewModel() {

    val serviciosActivos: StateFlow<List<Servicio>> = repository.observarServiciosActivos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _cantidades = MutableStateFlow<Map<Int, Double>>(emptyMap())
    val cantidades: StateFlow<Map<Int, Double>> = _cantidades.asStateFlow()

    private val _nota = MutableStateFlow("")
    val nota: StateFlow<String> = _nota.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    val subtotal: StateFlow<Double> = combine(serviciosActivos, cantidades) { servicios, mapCantidades ->
        servicios.sumOf { servicio ->
            val cant = mapCantidades[servicio.id] ?: 0.0
            cant * servicio.precio
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val totalKilos: StateFlow<Double> = combine(serviciosActivos, cantidades) { servicios, mapCantidades ->
        servicios.filter { it.unidad.equals("KG", ignoreCase = true) }
            .sumOf { mapCantidades[it.id] ?: 0.0 }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val descuento: StateFlow<Double> = combine(subtotal, totalKilos) { sub, kg ->
        if (kg > 5.0) sub * 0.10 else 0.0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val total: StateFlow<Double> = combine(subtotal, descuento) { sub, desc ->
        (sub - desc).coerceAtLeast(0.0)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val fechaEntregaEstimada: StateFlow<String> = combine(serviciosActivos, cantidades) { servicios, mapCantidades ->
        val seleccionados = servicios.filter { (mapCantidades[it.id] ?: 0.0) > 0.0 }
        if (seleccionados.isEmpty()) {
            "Selecciona un servicio"
        } else {
            val maxDias = 2L
            var f = LocalDate.now().plusDays(maxDias)
            if (f.dayOfWeek == DayOfWeek.SUNDAY) {
                f = f.plusDays(1)
            }
            val formatter = DateTimeFormatter.ofPattern("EEE d MMM", Locale("es", "ES"))
            "Entrega estimada: ${f.format(formatter)}"
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    fun precargarDesdePedido(pedidoConDetalle: PedidoConDetalle) {
        val mapaPreCargado = mutableMapOf<Int, Double>()
        pedidoConDetalle.detalles.forEach { d ->
            val idServicio = if (d.servicio.id > 0) d.servicio.id else d.detalle.servicioId
            if (d.detalle.cantidad > 0.0) {
                mapaPreCargado[idServicio] = d.detalle.cantidad
            }
        }
        _cantidades.value = mapaPreCargado
        if (pedidoConDetalle.pedido.nota.isNotBlank()) {
            _nota.value = pedidoConDetalle.pedido.nota
        }
        Log.d("REPETIR", "Precargadas cantidades: $mapaPreCargado")
    }

    fun cargarYPrecargarPedido(pedidoId: Int) {
        Log.d("REPETIR", "Cargando pedidoId: $pedidoId")
        viewModelScope.launch {
            repository.observarPedido(pedidoId).collect { pedidoConDetalle ->
                if (pedidoConDetalle != null) {
                    Log.d("REPETIR", "Pedido encontrado con id: ${pedidoConDetalle.pedido.id}, detalles: ${pedidoConDetalle.detalles.size}")
                    precargarDesdePedido(pedidoConDetalle)
                } else {
                    Log.d("REPETIR", "Pedido es NULL para id: $pedidoId")
                }
            }
        }
    }

    fun cambiarCantidad(servicio: Servicio, delta: Double) {
        val actual = _cantidades.value[servicio.id] ?: 0.0
        val unidadUpper = servicio.unidad.uppercase()

        val nuevaCantidad = if (unidadUpper == "KG") {
            when {
                actual == 0.0 && delta > 0 -> 1.0
                else -> (actual + delta).coerceIn(0.0, 10.0)
            }
        } else {
            (actual + delta).coerceIn(0.0, 30.0)
        }

        val nuevoMapa = _cantidades.value.toMutableMap()
        if (nuevaCantidad <= 0.0) {
            nuevoMapa.remove(servicio.id)
        } else {
            nuevoMapa[servicio.id] = nuevaCantidad
        }
        _cantidades.value = nuevoMapa
    }

    fun onNotaChanged(nuevaNota: String) {
        _nota.value = nuevaNota
    }

    fun limpiarError() {
        _mensajeError.value = null
    }

    fun confirmarPedido(onExito: () -> Unit) {
        viewModelScope.launch {
            val activos = repository.contarPedidosActivos(usuario.id)
            if (activos >= 3) {
                _mensajeError.value = "Ya tienes 3 pedidos activos"
                return@launch
            }

            val lineas = _cantidades.value.map { (servicioId, cant) ->
                LineaPedido(servicioId = servicioId, cantidad = cant)
            }.filter { it.cantidad > 0.0 }

            if (lineas.isEmpty()) return@launch

            val maxDias = 2L
            var f = LocalDate.now().plusDays(maxDias)
            if (f.dayOfWeek == DayOfWeek.SUNDAY) {
                f = f.plusDays(1)
            }
            val fechaStr = f.toString()

            repository.crearPedido(
                usuarioId = usuario.id,
                lineas = lineas,
                fechaEntrega = fechaStr,
                nota = _nota.value,
                descuento = descuento.value
            )

            onExito()
        }
    }
}