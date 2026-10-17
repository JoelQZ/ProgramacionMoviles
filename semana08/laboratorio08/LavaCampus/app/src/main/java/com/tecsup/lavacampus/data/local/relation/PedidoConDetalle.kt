package com.tecsup.lavacampus.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Usuario

data class PedidoConDetalle(
    @Embedded
    val pedido: Pedido,
    @Relation(
        parentColumn = "usuarioId",
        entityColumn = "id"
    )
    val usuario: Usuario,
    @Relation(
        entity = DetallePedido::class,
        parentColumn = "id",
        entityColumn = "pedidoId"
    )
    val detalles: List<DetalleConServicio>
)