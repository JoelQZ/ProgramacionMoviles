package com.tecsup.catalogo.data.mapper

import com.tecsup.catalogo.data.local.ItemEntity
import com.tecsup.catalogo.data.remote.dto.ItemDto
import com.tecsup.catalogo.domain.model.Item

fun ItemDto.aModelo(esFavorito: Boolean = false): Item = Item(
    id = id,
    titulo = title,
    subtitulo = "${brand ?: "Sin marca"} · \$$price",
    imagenUrl = thumbnail,
    esFavorito = esFavorito,
    descripcion = description,
    rating = rating,
    stock = stock
)

fun ItemDto.aEntity(esFavorito: Boolean = false): ItemEntity = ItemEntity(
    id = id,
    titulo = title,
    subtitulo = "${brand ?: "Sin marca"} · \$$price",
    imagenUrl = thumbnail,
    descripcion = description,
    rating = rating,
    stock = stock,
    esFavorito = esFavorito
)

fun ItemEntity.aModelo(): Item = Item(
    id = id,
    titulo = titulo,
    subtitulo = subtitulo,
    imagenUrl = imagenUrl,
    esFavorito = esFavorito,
    descripcion = descripcion,
    rating = rating,
    stock = stock
)
