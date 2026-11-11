package com.tecsup.catalogo.domain.model

data class Item(
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false,
    val descripcion: String = "",
    val rating: Double = 0.0,
    val stock: Int = 0
)
