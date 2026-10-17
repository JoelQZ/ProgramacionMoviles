package com.tecsup.lavacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tecsup.lavacampus.data.local.entity.Servicio
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(servicios: List<Servicio>)

    @Query("SELECT * FROM servicios WHERE activo = 1 ORDER BY nombre ASC")
    fun obtenerActivos(): Flow<List<Servicio>>

    @Query("SELECT * FROM servicios ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Servicio>>

    @Query("SELECT * FROM servicios WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Servicio?
}