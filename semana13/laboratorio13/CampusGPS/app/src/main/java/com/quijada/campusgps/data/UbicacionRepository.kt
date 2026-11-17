package com.quijada.campusgps.data

import com.quijada.campusgps.model.Posicion
import kotlinx.coroutines.flow.Flow

interface UbicacionRepository {
    suspend fun obtenerUbicacionActual(): Posicion?
    fun playServicesDisponible(): Boolean
    fun observarUbicacionEnVivo(intervaloMs: Long = 3000L): Flow<Result<Posicion>>
}
