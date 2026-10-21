package com.tecsup.lavacampus.data.repository

import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.ClienteResumen
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.IngresoServicio
import com.tecsup.lavacampus.model.LineaPedido
import kotlinx.coroutines.flow.Flow

interface LavaRepository {

    suspend fun resetearServiciosAPrototipo()

    suspend fun login(correo: String, clave: String): Usuario?
    fun observarClientes(): Flow<List<ClienteResumen>>

    fun observarServiciosActivos(): Flow<List<Servicio>>
    fun observarTodosLosServicios(): Flow<List<Servicio>>
    suspend fun servicioTieneDetalles(servicioId: Int): Boolean

    suspend fun crearPedido(
        usuarioId: Int,
        lineas: List<LineaPedido>,
        fechaEntrega: String,
        nota: String
    ): Long

    suspend fun cambiarEstado(pedidoId: Int, nuevoEstado: EstadoPedido, casillero: String?)
    suspend fun actualizarPagado(pedidoId: Int, pagado: Boolean)
    suspend fun obtenerPedido(pedidoId: Int): Pedido?

    fun observarPedidosPorUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>>
    fun observarTodosLosPedidos(): Flow<List<PedidoConDetalle>>
    fun observarPedido(pedidoId: Int): Flow<PedidoConDetalle?>
    suspend fun contarPedidosActivos(usuarioId: Int): Int
    fun observarCasillerosOcupados(): Flow<List<String>>

    suspend fun contarPedidosPorFecha(fecha: String): Int
    suspend fun contarPedidosPorEstado(estado: EstadoPedido): Int
    suspend fun contarPedidosAtrasados(fechaHoy: String): Int
    fun observarIngresosDesde(fechaInicio: String): Flow<Double>
    fun observarIngresosPorServicio(fechaInicio: String): Flow<List<IngresoServicio>>
    suspend fun insertarServicio(servicio: Servicio)
    suspend fun actualizarServicio(servicio: Servicio)
    suspend fun eliminarServicio(servicio: Servicio)
}