package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MisPedidosViewModel(
    private val repository: LavaRepository,
    private val usuario: Usuario
) : ViewModel() {

    private val todosLosPedidos: StateFlow<List<PedidoConDetalle>> =
        repository.observarPedidosPorUsuario(usuario.id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val activos: StateFlow<List<PedidoConDetalle>> = todosLosPedidos.map { lista ->
        lista.filter { p ->
            val est = p.pedido.estado.uppercase()
            est == "RECIBIDO" || est == "EN_PROCESO" || est == "LISTO"
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val historial: StateFlow<List<PedidoConDetalle>> = todosLosPedidos.map { lista ->
        lista.filter { p ->
            val est = p.pedido.estado.uppercase()
            est == "ENTREGADO" || est == "CANCELADO"
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun cancelarPedido(pedidoId: Int, casillero: String?) {
        viewModelScope.launch {
            repository.cambiarEstado(pedidoId, EstadoPedido.CANCELADO, casillero)
        }
    }
}