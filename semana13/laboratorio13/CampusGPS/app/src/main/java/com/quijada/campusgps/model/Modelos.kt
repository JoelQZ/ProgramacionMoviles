package com.quijada.campusgps.model

data class Posicion(
    val latitud: Double,
    val longitud: Double
)

data class Lugar(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double
)

data class LugarConDistancia(
    val lugar: Lugar,
    val metros: Float? = null,
    val dentroDelRadio: Boolean = false
)

enum class TipoError {
    SIN_PERMISO,
    GPS_APAGADO,
    SIN_PLAY_SERVICES,
    OTRO
}

sealed interface UbicacionUi {
    data object Inicial : UbicacionUi
    data object Buscando : UbicacionUi
    data class Lista(val posicion: Posicion) : UbicacionUi
    data class Error(
        val mensaje: String,
        val tipo: TipoError = TipoError.OTRO
    ) : UbicacionUi
}
