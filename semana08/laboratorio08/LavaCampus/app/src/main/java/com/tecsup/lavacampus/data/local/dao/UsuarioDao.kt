package com.tecsup.lavacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.ClienteResumen
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(lista: List<Usuario>)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Query("""
        SELECT u.id, u.nombre, u.codigo, u.correo, 
               COUNT(p.id) AS totalPedidos, 
               COALESCE(SUM(CASE WHEN p.pagado = 1 THEN p.total ELSE 0 END), 0.0) AS totalMonto
        FROM usuarios u
        LEFT JOIN pedidos p ON u.id = p.usuarioId AND p.estado != 'CANCELADO'
        WHERE u.rol = 'CLIENTE'
        GROUP BY u.id
    """)
    fun observarClientes(): Flow<List<ClienteResumen>>
}