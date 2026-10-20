package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GestionPedidoViewModel(
    private val repository: LavaRepository,
    private val pedidoId: Int
) : ViewModel() {

    val pedidoConDetalle: StateFlow<PedidoConDetalle?> = repository.observarPedido(pedidoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val casillerosOcupados: StateFlow<List<String>> = repository.observarCasillerosOcupados()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val casilleroElegido = MutableStateFlow<String?>(null)
    val estaPagado = MutableStateFlow(false)
    val mensajeError = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            pedidoConDetalle.collect { item ->
                if (item != null) {
                    if (casilleroElegido.value == null) {
                        casilleroElegido.value = item.pedido.casillero
                    }
                    estaPagado.value = item.pedido.pagado
                }
            }
        }
    }

    fun elegirCasillero(c: String) {
        casilleroElegido.value = c
        mensajeError.value = null
    }

    fun marcarPagado(b: Boolean) {
        estaPagado.value = b
        mensajeError.value = null
        viewModelScope.launch {
            repository.actualizarPagado(pedidoId, b)
        }
    }

    fun avanzar() {
        val item = pedidoConDetalle.value ?: return
        val estActual = item.pedido.estado.uppercase()

        when (estActual) {
            "RECIBIDO" -> {
                viewModelScope.launch {
                    repository.cambiarEstado(pedidoId, EstadoPedido.EN_PROCESO, item.pedido.casillero)
                }
            }
            "EN_PROCESO" -> {
                val cas = casilleroElegido.value
                if (cas.isNullOrBlank()) {
                    mensajeError.value = "Debes asignar un casillero para marcar como Listo."
                    return
                }
                viewModelScope.launch {
                    repository.cambiarEstado(pedidoId, EstadoPedido.LISTO, cas)
                }
            }
            "LISTO" -> {
                if (!estaPagado.value) {
                    mensajeError.value = "No se puede entregar un pedido sin cobrar."
                    return
                }
                viewModelScope.launch {
                    repository.cambiarEstado(pedidoId, EstadoPedido.ENTREGADO, item.pedido.casillero)
                }
            }
        }
    }

    fun cancelar() {
        val item = pedidoConDetalle.value ?: return
        val est = item.pedido.estado.uppercase()
        if (est == "RECIBIDO" || est == "EN_PROCESO") {
            viewModelScope.launch {
                repository.cambiarEstado(pedidoId, EstadoPedido.CANCELADO, item.pedido.casillero)
            }
        }
    }
}