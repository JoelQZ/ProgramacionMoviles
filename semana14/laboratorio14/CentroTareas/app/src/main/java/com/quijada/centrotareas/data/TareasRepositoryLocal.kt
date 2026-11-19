package com.quijada.centrotareas.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TareasRepositoryLocal : TareasRepository {
    override fun ejecutarTareaConProgreso(): Flow<Float> = flow {
        Log.d("HILO", "Flujo en hilo: ${Thread.currentThread().name}")
        for (i in 1..100) {
            delay(40)
            emit(i / 100f)
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun contarPrimos(hasta: Int): Int = withContext(Dispatchers.Default) {
        var contador = 0
        for (n in 2..hasta) {
            ensureActive()
            if (esPrimo(n)) {
                contador++
            }
        }
        contador
    }

    private fun esPrimo(n: Int): Boolean {
        if (n < 2) return false
        var d = 2
        while (d.toLong() * d <= n) {
            if (n % d == 0) return false
            d++
        }
        return true
    }

    override suspend fun cargarPerfil(): String = withContext(Dispatchers.IO) {
        delay(2000)
        "Ana Torres (ficticia)"
    }

    override suspend fun cargarPedidos(simularError: Boolean): List<String> = withContext(Dispatchers.IO) {
        delay(3000)
        if (simularError) {
            throw Exception("Sin conexión")
        }
        listOf("Pedido 1", "Pedido 2", "Pedido 3")
    }
}
