package com.quijada.campusgps.data

import com.quijada.campusgps.model.Lugar

interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>
}
