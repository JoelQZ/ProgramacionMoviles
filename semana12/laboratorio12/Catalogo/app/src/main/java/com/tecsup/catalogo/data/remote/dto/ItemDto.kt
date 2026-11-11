package com.tecsup.catalogo.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductosRespuesta(
    val products: List<ItemDto>
)

@Serializable
data class ItemDto(
    val id: Int,
    val title: String,
    val description: String = "",
    val brand: String? = null,
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val stock: Int = 0,
    val thumbnail: String? = null
)
