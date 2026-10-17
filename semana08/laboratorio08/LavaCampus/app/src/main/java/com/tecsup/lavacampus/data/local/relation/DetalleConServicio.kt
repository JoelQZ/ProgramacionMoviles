package com.tecsup.lavacampus.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Servicio

data class DetalleConServicio(
    @Embedded
    val detalle: DetallePedido,
    @Relation(
        parentColumn = "servicioId",
        entityColumn = "id"
    )
    val servicio: Servicio
)