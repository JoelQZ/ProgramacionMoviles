package com.quijada.campusgps.data

import com.quijada.campusgps.model.Posicion

interface UbicacionRepository {
    suspend fun obtenerUbicacionActual(): Posicion?
}
