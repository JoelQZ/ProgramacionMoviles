package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

class BandejaViewModel(
    private val repository: LavaRepository,
    private val usuarioIdInicial: Int? = null
) : ViewModel() {

    private val hoyStr = LocalDate.now().toString()

    val textoBusqueda = MutableStateFlow("")
    val filtroEstado = MutableStateFlow("Todos")

    private val todosLosPedidos = repository.observarTodosLosPedidos()

    val pedidos: StateFlow<List<PedidoConDetalle>> = combine(
        todosLosPedidos,
        textoBusqueda,
        filtroEstado
    ) { lista, busqueda, estado ->
        lista.filter { item ->
            val p = item.pedido
            val u = item.usuario

            val cumpleUsuario = if (usuarioIdInicial != null && usuarioIdInicial > 0) {
                p.usuarioId == usuarioIdInicial
            } else {
                true
            }

            val estUpper = p.estado.uppercase()
            val esAtrasado = p.fechaEntrega < hoyStr && estUpper != "ENTREGADO" && estUpper != "CANCELADO"

            val cumpleEstado = when (estado) {
                "Todos" -> true
                "Atrasados" -> esAtrasado
                else -> p.estado.equals(estado, ignoreCase = true)
            }

            val query = busqueda.trim().lowercase(Locale.getDefault())
            val numPedidoStr = String.format(Locale.US, "%03d", p.id)
            val nombreEstudiante = u?.nombre?.lowercase(Locale.getDefault()) ?: ""

            val cumpleBusqueda = query.isEmpty() ||
                    numPedidoStr.contains(query) ||
                    nombreEstudiante.contains(query)

            cumpleUsuario && cumpleEstado && cumpleBusqueda
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun actualizarBusqueda(query: String) {
        textoBusqueda.value = query
    }

    fun seleccionarFiltro(estado: String) {
        filtroEstado.value = estado
    }

    fun avanzarEstadoRapido(item: PedidoConDetalle) {
        val nuevoEstado = when (item.pedido.estado.uppercase()) {
            "RECIBIDO" -> EstadoPedido.EN_PROCESO
            else -> return
        }
        viewModelScope.launch {
            repository.cambiarEstado(item.pedido.id, nuevoEstado, item.pedido.casillero)
        }
    }
}