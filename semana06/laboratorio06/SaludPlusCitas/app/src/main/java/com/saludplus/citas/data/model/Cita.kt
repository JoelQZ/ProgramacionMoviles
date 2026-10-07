package com.saludplus.citas.data.model

data class Cita(
    val id: String,
    val usuarioEmail: String,
    val medicoId: String,
    val especialidadId: String,
    val fecha: String,
    val hora: String,
    val direccion: String,
    val motivo: String
)