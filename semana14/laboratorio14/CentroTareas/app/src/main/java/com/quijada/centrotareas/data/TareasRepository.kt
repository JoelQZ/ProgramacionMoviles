package com.quijada.centrotareas.data

import kotlinx.coroutines.flow.Flow

interface TareasRepository {
    fun ejecutarTareaConProgreso(): Flow<Float>
    suspend fun contarPrimos(hasta: Int, onProgreso: (Float) -> Unit = {}): Int
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}
