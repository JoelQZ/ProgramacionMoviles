package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetallePedidoViewModel(
    private val repository: LavaRepository,
    private val pedidoId: Int
) : ViewModel() {

    val pedido: StateFlow<PedidoConDetalle?> = repository.observarPedido(pedidoId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val puedeCancelar: StateFlow<Boolean> = pedido.map { p ->
        val estadoActual = p?.pedido?.estado
        estadoActual.equals(EstadoPedido.RECIBIDO.name, ignoreCase = true) ||
                estadoActual.equals("RECIBIDO", ignoreCase = true)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun cancelarPedido(onExito: () -> Unit) {
        viewModelScope.launch {
            val actual = pedido.value?.pedido
            val esRecibido = actual?.estado.equals(EstadoPedido.RECIBIDO.name, ignoreCase = true) ||
                    actual?.estado.equals("RECIBIDO", ignoreCase = true)

            if (actual != null && esRecibido) {
                repository.cambiarEstado(pedidoId, EstadoPedido.CANCELADO, actual.casillero)
                onExito()
            }
        }
    }
}