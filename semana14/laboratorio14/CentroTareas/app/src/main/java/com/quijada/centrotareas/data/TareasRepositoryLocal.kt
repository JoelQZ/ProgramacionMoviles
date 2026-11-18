package com.quijada.centrotareas.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class TareasRepositoryLocal : TareasRepository {
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

    override suspend fun cargarPerfil(): String {
        return "Ana Torres (ficticia)"
    }

    override suspend fun cargarPedidos(simularError: Boolean): List<String> {
        if (simularError) {
            throw Exception("Sin conexión")
        }
        return listOf("Pedido 1", "Pedido 2", "Pedido 3")
    }
}
