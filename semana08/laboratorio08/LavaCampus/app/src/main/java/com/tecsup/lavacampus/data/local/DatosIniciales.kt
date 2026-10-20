package com.tecsup.lavacampus.data.local

import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.Rol
import java.time.LocalDate

object DatosIniciales {

    suspend fun sembrar(db: LavaDatabase) {
        if (db.usuarioDao().contar() == 0) {
            val hoy = LocalDate.now().toString()
            val hace3Dias = LocalDate.now().minusDays(3).toString()

            val usuarios = listOf(
                Usuario(
                    id = 1,
                    nombre = "Facundo Zevallos",
                    codigo = "ADM001",
                    correo = "facundo.zevallos@admin.com",
                    clave = "12345",
                    rol = Rol.ADMIN.name
                ),
                Usuario(
                    id = 2,
                    nombre = "Juan Pérez",
                    codigo = "20230001",
                    correo = "juan.perez@tecsup.edu.pe",
                    clave = "123456",
                    rol = Rol.CLIENTE.name
                ),
                Usuario(
                    id = 3,
                    nombre = "Maria López",
                    codigo = "20230002",
                    correo = "maria.lopez@tecsup.edu.pe",
                    clave = "123456",
                    rol = Rol.CLIENTE.name
                ),
                Usuario(
                    id = 4,
                    nombre = "Joel Quijada",
                    codigo = "20230003",
                    correo = "joel.quijada@tecsup.edu.pe",
                    clave = "123456",
                    rol = Rol.CLIENTE.name
                )
            )
            db.usuarioDao().insertarTodos(usuarios)

            val servicios = listOf(
                Servicio(id = 1, nombre = "Lavado x Kilo", descripcion = "Ropa general lavada y doblada", precio = 4.50, unidad = "KG", categoria = "Lavado", activo = true),
                Servicio(id = 2, nombre = "Planchado de Camisa", descripcion = "Planchado a vapor impecable", precio = 3.00, unidad = "UND", categoria = "Planchado", activo = true),
                Servicio(id = 3, nombre = "Edredón 2 Plazas", descripcion = "Lavado especial y secado", precio = 18.00, unidad = "UND", categoria = "Especial", activo = true)
            )
            db.servicioDao().insertarTodos(servicios)

            val p1 = Pedido(id = 1, usuarioId = 2, fechaRegistro = hace3Dias, fechaEntrega = hoy, estado = EstadoPedido.ENTREGADO.name, total = 12.00, casillero = "C-01", pagado = true)
            val p2 = Pedido(id = 2, usuarioId = 3, fechaRegistro = hoy, fechaEntrega = hoy, estado = EstadoPedido.LISTO.name, total = 18.00, casillero = "C-02", pagado = false)

            db.pedidoDao().insertar(p1)
            db.pedidoDao().insertar(p2)

            val detalles = listOf(
                DetallePedido(id = 1, pedidoId = 1, servicioId = 1, cantidad = 2.0, precioUnitario = 4.50, subtotal = 9.00),
                DetallePedido(id = 2, pedidoId = 1, servicioId = 2, cantidad = 1.0, precioUnitario = 3.00, subtotal = 3.00),
                DetallePedido(id = 3, pedidoId = 2, servicioId = 3, cantidad = 1.0, precioUnitario = 18.00, subtotal = 18.00)
            )
            db.detalleDao().insertarTodos(detalles)
        }
    }
}