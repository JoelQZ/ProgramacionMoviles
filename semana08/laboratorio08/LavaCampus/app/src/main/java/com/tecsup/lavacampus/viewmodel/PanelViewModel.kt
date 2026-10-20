package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.graficos.DatoGrafico
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class PanelViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    private val hoyStr = LocalDate.now().toString()

    private val inicioSemanaStr: String = run {
        val hoy = LocalDate.now()
        if (hoy.dayOfWeek == DayOfWeek.SUNDAY) {
            hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString()
        } else {
            hoy.with(DayOfWeek.MONDAY).toString()
        }
    }

    private val todosLosPedidos = repository.observarTodosLosPedidos()

    val pedidosHoy: StateFlow<Int> = todosLosPedidos
        .map { lista -> lista.count { it.pedido.fechaRegistro == hoyStr } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val enProceso: StateFlow<Int> = todosLosPedidos
        .map { lista -> lista.count { it.pedido.estado.uppercase() == EstadoPedido.EN_PROCESO.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val porRecoger: StateFlow<Int> = todosLosPedidos
        .map { lista -> lista.count { it.pedido.estado.uppercase() == EstadoPedido.LISTO.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val ingresosSemana: StateFlow<Double> = todosLosPedidos
        .map { lista ->
            lista.filter { it.pedido.fechaRegistro >= inicioSemanaStr }
                .sumOf { it.pedido.total }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalPedidosSemana: StateFlow<Int> = todosLosPedidos
        .map { lista -> lista.count { it.pedido.fechaRegistro >= inicioSemanaStr } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val porEstado: StateFlow<List<DatoGrafico>> = todosLosPedidos
        .map { lista ->
            val recibido = lista.count { it.pedido.estado.uppercase() == EstadoPedido.RECIBIDO.name }.toFloat()
            val enProceso = lista.count { it.pedido.estado.uppercase() == EstadoPedido.EN_PROCESO.name }.toFloat()
            val listo = lista.count { it.pedido.estado.uppercase() == EstadoPedido.LISTO.name }.toFloat()
            val entregado = lista.count { it.pedido.estado.uppercase() == EstadoPedido.ENTREGADO.name }.toFloat()

            listOf(
                DatoGrafico("Recibido", recibido, EstadoPedido.RECIBIDO.color),
                DatoGrafico("En proceso", enProceso, EstadoPedido.EN_PROCESO.color),
                DatoGrafico("Listo", listo, EstadoPedido.LISTO.color),
                DatoGrafico("Entregado", entregado, EstadoPedido.ENTREGADO.color)
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val porServicio: StateFlow<List<DatoGrafico>> = todosLosPedidos
        .map { lista ->
            val mapa = mutableMapOf<String, Double>()
            lista.forEach { p ->
                p.detalles.forEach { d ->
                    val nom = d.servicio.nombre
                    mapa[nom] = (mapa[nom] ?: 0.0) + d.detalle.subtotal
                }
            }
            mapa.map { (nombre, total) ->
                DatoGrafico(nombre, total.toFloat(), EstadoPedido.EN_PROCESO.color)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}