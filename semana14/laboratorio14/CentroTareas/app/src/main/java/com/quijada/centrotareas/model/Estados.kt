package com.quijada.centrotareas.model

sealed interface PrimosUi {
    data object Inactivo : PrimosUi
    data object Calculando : PrimosUi
    data class Listo(
        val hasta: Int,
        val cantidad: Int,
        val duracionMs: Long
    ) : PrimosUi
    data object Cancelado : PrimosUi
}

sealed interface CargaUi {
    data object Idle : CargaUi
    data object Cargando : CargaUi
    data class Exito(
        val modo: String,
        val perfil: String,
        val cantidadPedidos: Int,
        val duracionMs: Long
    ) : CargaUi
    data class Error(val mensaje: String) : CargaUi
}

data class ComparacionFila(
    val corrida: Int,
    val tiempoSecuencialMs: Long? = null,
    val tiempoConcurrenteMs: Long? = null
) {
    val ahorroMs: Long?
        get() = if (tiempoSecuencialMs != null && tiempoConcurrenteMs != null) {
            tiempoSecuencialMs - tiempoConcurrenteMs
        } else null
}
