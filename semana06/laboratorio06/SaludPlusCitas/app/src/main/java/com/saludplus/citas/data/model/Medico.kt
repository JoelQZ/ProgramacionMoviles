package com.saludplus.citas.data.model

data class Medico(
    val id: String,
    val especialidadId: String,
    val nombre: String,
    val especialidadNombre: String,
    val cmp: String,
    val calificacion: Double,
    val resenas: Int
)