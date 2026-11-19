package com.quijada.centrotareas.data

interface TareasRepository {
    suspend fun contarPrimos(hasta: Int): Int
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}
