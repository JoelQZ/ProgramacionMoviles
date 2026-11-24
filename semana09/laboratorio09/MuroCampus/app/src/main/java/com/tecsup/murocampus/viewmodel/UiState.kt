package com.tecsup.murocampus.viewmodel

import java.io.IOException
import kotlinx.serialization.SerializationException

sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Exito<T>(val datos: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}

fun mensajeDe(e: Throwable): String = when {
    e is IOException -> "Sin conexión a internet.\nRevisa tu red e inténtalo otra vez."
    e::class.java.name.endsWith("HttpException") -> "Error del servidor"
    e is SerializationException -> "Respuesta inesperada"
    else -> "Error desconocido"
}
