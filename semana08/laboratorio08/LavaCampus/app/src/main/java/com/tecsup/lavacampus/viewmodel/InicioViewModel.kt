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

class InicioViewModel(
    repository: LavaRepository,
    usuario: Usuario
) : ViewModel() {

    private val pedidosUsuario = repository.observarPedidosPorUsuario(usuario.id)

    val pedidoActivo: StateFlow<PedidoConDetalle?> = pedidosUsuario.map { lista ->
        lista.firstOrNull { item ->
            val estado = try {
                EstadoPedido.valueOf(item.pedido.estado)
            } catch (e: Exception) {
                null
            }
            estado == EstadoPedido.RECIBIDO ||
                    estado == EstadoPedido.EN_PROCESO ||
                    estado == EstadoPedido.LISTO
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val recientes: StateFlow<List<PedidoConDetalle>> = pedidosUsuario.map { lista ->
        lista.take(2)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}