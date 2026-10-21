package com.tecsup.lavacampus.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes")

class PreferenciasStore(private val context: Context) {

    companion object {
        val PERFIL_ACTIVO = stringPreferencesKey("perfil_activo")
        val USUARIO_ID = intPreferencesKey("usuario_id")
        val MODO_OSCURO = booleanPreferencesKey("modo_oscuro")
        val NOTIFICACIONES = booleanPreferencesKey("notificaciones")
        val NOTIFICACIONES_ESTUDIANTE = booleanPreferencesKey("notificaciones_estudiante")
        val NOTIFICACIONES_ADMIN = booleanPreferencesKey("notificaciones_admin")
        val RECIBIR_PEDIDOS = booleanPreferencesKey("recibir_pedidos")
    }

    val perfilActivo: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PERFIL_ACTIVO] ?: ""
    }

    val usuarioId: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[USUARIO_ID] ?: -1
    }

    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[MODO_OSCURO] ?: false
    }

    val notificacionesEstudiante: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICACIONES_ESTUDIANTE] ?: preferences[NOTIFICACIONES] ?: true
    }

    val notificacionesAdmin: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICACIONES_ADMIN] ?: true
    }

    val notificaciones: Flow<Boolean> = notificacionesEstudiante

    val recibirPedidos: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[RECIBIR_PEDIDOS] ?: true
    }

    suspend fun guardarSesion(rol: String, usuarioId: Int) {
        context.dataStore.edit { preferences ->
            preferences[PERFIL_ACTIVO] = rol
            preferences[USUARIO_ID] = usuarioId
        }
    }

    suspend fun guardarModoOscuro(b: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MODO_OSCURO] = b
        }
    }

    suspend fun guardarNotificacionesEstudiante(b: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICACIONES_ESTUDIANTE] = b
            preferences[NOTIFICACIONES] = b
        }
    }

    suspend fun guardarNotificacionesAdmin(b: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICACIONES_ADMIN] = b
        }
    }

    suspend fun guardarNotificaciones(b: Boolean) {
        guardarNotificacionesEstudiante(b)
    }

    suspend fun guardarRecibirPedidos(b: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[RECIBIR_PEDIDOS] = b
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { preferences ->
            preferences[PERFIL_ACTIVO] = ""
            preferences[USUARIO_ID] = -1
        }
    }
}