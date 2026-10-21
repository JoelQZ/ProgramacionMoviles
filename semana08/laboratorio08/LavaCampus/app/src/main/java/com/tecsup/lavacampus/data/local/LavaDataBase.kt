package com.tecsup.lavacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tecsup.lavacampus.data.local.dao.DetalleDao
import com.tecsup.lavacampus.data.local.dao.PedidoDao
import com.tecsup.lavacampus.data.local.dao.ServicioDao
import com.tecsup.lavacampus.data.local.dao.UsuarioDao
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Servicio::class,
        Usuario::class,
        Pedido::class,
        DetallePedido::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LavaDatabase : RoomDatabase() {

    abstract fun servicioDao(): ServicioDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun detalleDao(): DetalleDao

    companion object {
        @Volatile
        private var INSTANCE: LavaDatabase? = null

        fun obtener(context: Context): LavaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LavaDatabase::class.java,
                    "lavacampus.db"
                )
                    .addCallback(LavaDatabaseCallback())
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    private class LavaDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    precargarServicios(database.servicioDao())
                }
            }
        }

        suspend fun precargarServicios(servicioDao: ServicioDao) {
            val serviciosIniciales = listOf(
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

            servicioDao.insertarTodos(serviciosIniciales)
        }
    }
}