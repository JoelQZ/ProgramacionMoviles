package com.tecsup.lavacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tecsup.lavacampus.data.local.entity.Servicio
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(servicio: Servicio)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(servicios: List<Servicio>)

    @Update
    suspend fun actualizar(servicio: Servicio)

    @Delete
    suspend fun eliminar(servicio: Servicio)

    @Query("SELECT * FROM servicios WHERE activo = 1 ORDER BY nombre ASC")
    fun obtenerActivos(): Flow<List<Servicio>>

    @Query("SELECT * FROM servicios ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Servicio>>

    @Query("SELECT * FROM servicios WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Servicio?
}