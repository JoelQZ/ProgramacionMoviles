package com.quijada.centrotareas.data

class TareasRepositoryLocal : TareasRepository {
    override suspend fun contarPrimos(hasta: Int): Int {
        return 0
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
