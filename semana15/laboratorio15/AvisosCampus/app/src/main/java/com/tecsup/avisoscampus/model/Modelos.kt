package com.tecsup.avisoscampus.model

enum class Origen(val texto: String) {
    BOTON("Botón"),
    RECORDATORIO("Recordatorio"),
    MODO_AVION("Modo avión")
}

data class Aviso(
    val id: Int,
    val titulo: String,
    val texto: String,
    val origen: Origen,
    val hora: String,
    val esUrgente: Boolean = false
)

sealed interface PermisoUi {
    object SinPedir : PermisoUi
    object Concedido : PermisoUi
    object Denegado : PermisoUi
}
