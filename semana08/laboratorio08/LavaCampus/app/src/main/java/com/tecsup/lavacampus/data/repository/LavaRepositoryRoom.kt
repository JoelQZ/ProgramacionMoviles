package com.tecsup.lavacampus.data.repository

import androidx.room.withTransaction
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.ClienteResumen
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.IngresoServicio
import com.tecsup.lavacampus.model.LineaPedido
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class LavaRepositoryRoom(
    private val db: LavaDatabase
) : LavaRepository {

    private val usuarioDao = db.usuarioDao()
    private val servicioDao = db.servicioDao()
    private val pedidoDao = db.pedidoDao()
    private val detalleDao = db.detalleDao()

    override suspend fun resetearServiciosAPrototipo() {
        db.withTransaction {
            val serviciosCorrectos = listOf(
                Servicio(
                    id = 1,
                    nombre = "Lavado y secado",
                    descripcion = "Servicio completo de lavado y secado",
                    precio = 8.00,
                    unidad = "kg",
                    categoria = "Lavado",
                    activo = true
                ),
                Servicio(
                    id = 2,
                    nombre = "Planchado",
                    descripcion = "Planchado por prenda",
                    precio = 2.50,
                    unidad = "prenda",
                    categoria = "Planchado",
                    activo = true
                ),
                Servicio(
                    id = 3,
                    nombre = "Lavado en seco",
                    descripcion = "Tratamiento especial para prendas delicadas",
                    precio = 12.00,
                    unidad = "prenda",
                    categoria = "Seco",
                    activo = true
                )
            )
            servicioDao.insertarTodos(serviciosCorrectos)
        }
    }

    override suspend fun login(correo: String, clave: String): Usuario? {
        return usuarioDao.login(correo, clave)
    }

    override fun observarClientes(): Flow<List<ClienteResumen>> {
        return usuarioDao.observarClientes()
    }

    override fun observarServiciosActivos(): Flow<List<Servicio>> {
        return servicioDao.obtenerActivos()
    }

    override fun observarTodosLosServicios(): Flow<List<Servicio>> {
        return servicioDao.obtenerTodos()
    }

    override suspend fun servicioTieneDetalles(servicioId: Int): Boolean {
        return detalleDao.contarPorServicio(servicioId) > 0
    }

    override suspend fun insertarServicio(servicio: Servicio) {
        servicioDao.insertar(servicio)
    }

    override suspend fun actualizarServicio(servicio: Servicio) {
        servicioDao.actualizar(servicio)
    }

    override suspend fun eliminarServicio(servicio: Servicio) {
        servicioDao.eliminar(servicio)
    }

    override suspend fun crearPedido(
        usuarioId: Int,
        lineas: List<LineaPedido>,
        fechaEntrega: String,
        nota: String,
        descuento: Double
    ): Long {
        return db.withTransaction {
            var subtotalCalculado = 0.0
            val detallesAInsertar = mutableListOf<Pair<Int, Double>>()

            for (linea in lineas) {
                val servicio = db.servicioDao().obtenerPorId(linea.servicioId)
                    ?: throw IllegalArgumentException("Servicio no encontrado")
                val subtotal = servicio.precio * linea.cantidad
                subtotalCalculado += subtotal
                detallesAInsertar.add(Pair(linea.servicioId, linea.cantidad))
            }

            val totalFinal = (subtotalCalculado - descuento).coerceAtLeast(0.0)

            val hoy = LocalDate.now().toString()
            val nuevoPedido = Pedido(
                usuarioId = usuarioId,
                fechaRegistro = hoy,
                fechaEntrega = fechaEntrega,
                estado = EstadoPedido.RECIBIDO.name,
                total = totalFinal,
                descuento = descuento,
                casillero = null,
                pagado = false,
                nota = nota
            )

            val pedidoId = pedidoDao.insertar(nuevoPedido)

            val listaDetalles = detallesAInsertar.map { (servicioId, cantidad) ->
                val servicio = db.servicioDao().obtenerPorId(servicioId)!!
                DetallePedido(
                    pedidoId = pedidoId.toInt(),
                    servicioId = servicioId,
                    cantidad = cantidad,
                    precioUnitario = servicio.precio,
                    subtotal = servicio.precio * cantidad
                )
            }

            detalleDao.insertarTodos(listaDetalles)
            pedidoId
        }
    }

    override suspend fun cambiarEstado(pedidoId: Int, nuevoEstado: EstadoPedido, casillero: String?) {
        pedidoDao.actualizarEstado(pedidoId, nuevoEstado.name, casillero)
    }

    override suspend fun actualizarPagado(pedidoId: Int, pagado: Boolean) {
        pedidoDao.actualizarPagado(pedidoId, pagado)
    }

    override suspend fun obtenerPedido(pedidoId: Int): Pedido? {
        return pedidoDao.obtener(pedidoId)
    }

    override fun observarPedidosPorUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>> {
        return pedidoDao.observarPorUsuario(usuarioId)
    }

    override fun observarTodosLosPedidos(): Flow<List<PedidoConDetalle>> {
        return pedidoDao.observarTodos()
    }

    override fun observarPedido(pedidoId: Int): Flow<PedidoConDetalle?> {
        return pedidoDao.observarUno(pedidoId)
    }

    override suspend fun contarPedidosActivos(usuarioId: Int): Int {
        return pedidoDao.contarActivos(usuarioId)
    }

    override fun observarCasillerosOcupados(): Flow<List<String>> {
        return pedidoDao.observarCasillerosOcupados()
    }

    override suspend fun contarPedidosPorFecha(fecha: String): Int {
        return pedidoDao.contarPorFecha(fecha)
    }

    override suspend fun contarPedidosPorEstado(estado: EstadoPedido): Int {
        return pedidoDao.contarPorEstado(estado.name)
    }

    override suspend fun contarPedidosAtrasados(fechaHoy: String): Int {
        return pedidoDao.contarAtrasados(fechaHoy)
    }

    override fun observarIngresosDesde(fechaInicio: String): Flow<Double> {
        return pedidoDao.ingresosDesde(fechaInicio)
    }

    override fun observarIngresosPorServicio(fechaInicio: String): Flow<List<IngresoServicio>> {
        return detalleDao.observarIngresosPorServicio(fechaInicio)
    }
}