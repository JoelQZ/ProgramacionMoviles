package com.tecsup.lavacampus.model

data class ClienteResumen(
    val id: Int,
    val nombre: String,
    val codigo: String,
    val correo: String,
    val totalPedidos: Int,
    val totalMonto: Double
)