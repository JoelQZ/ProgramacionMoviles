package com.quijada.centrotareas.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "historial_tareas")
private val KEY_HISTORIAL = stringPreferencesKey("ultimos_5")

class TareasRepositoryLocal(
    private val context: Context
) : TareasRepository {

    override val historialResultados: Flow<List<String>> = context.dataStore.data.map { preferences ->
        val raw = preferences[KEY_HISTORIAL] ?: ""
        if (raw.isBlank()) emptyList() else raw.split("||")
    }

    override suspend fun guardarResultado(registro: String) {
        context.dataStore.edit { preferences ->
            val actual = preferences[KEY_HISTORIAL] ?: ""
            val listaActual = if (actual.isBlank()) emptyList() else actual.split("||")
            val nuevaLista = (listOf(registro) + listaActual).take(5)
            preferences[KEY_HISTORIAL] = nuevaLista.joinToString("||")
        }
    }

    override suspend fun limpiarHistorial() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_HISTORIAL)
        }
    }

    override fun ejecutarTareaConProgreso(): Flow<Float> = flow {
        Log.d("HILO", "Flujo en hilo: ${Thread.currentThread().name}")
        for (i in 1..100) {
            delay(40)
            emit(i / 100f)
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun contarPrimos(hasta: Int, onProgreso: (Float) -> Unit): Int = withContext(Dispatchers.Default) {
        var contador = 0
        val paso = (hasta / 100).coerceAtLeast(1)
        for (n in 2..hasta) {
            currentCoroutineContext().ensureActive()
            if (esPrimo(n)) {
                contador++
            }
            if (n % paso == 0 || n == hasta) {
                onProgreso(n.toFloat() / hasta)
            }
        }
        onProgreso(1f)
        contador
    }

    private fun esPrimo(n: Int): Boolean {
        if (n < 2) return false
        var d = 2
        while (d.toLong() * d <= n) {
            if (n % d == 0) return false
            d++
        }
        return true
    }

    override suspend fun cargarPerfil(): String = withContext(Dispatchers.IO) {
        delay(2000)
        "Ana Torres (ficticia)"
    }

    override suspend fun cargarPedidos(simularError: Boolean): List<String> = withContext(Dispatchers.IO) {
        reintentarConBackoff(intentosMax = 3, delayInicialMs = 500L) {
            delay(3000)
            if (simularError) {
                throw Exception("Sin conexión")
            }
            listOf("Pedido 1", "Pedido 2", "Pedido 3")
        }
    }

    private suspend fun <T> reintentarConBackoff(
        intentosMax: Int = 3,
        delayInicialMs: Long = 500L,
        block: suspend () -> T
    ): T {
        for (intento in 0 until intentosMax) {
            try {
                return block()
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                if (intento == intentosMax - 1) {
                    throw e
                }
                val espera = delayInicialMs * (1 shl intento)
                Log.w(
                    "REINTENTO",
                    "Intento ${intento + 1} de $intentosMax fallido. Reintentando en ${espera}ms..."
                )
                delay(espera)
            }
        }
        error("Inalcanzable")
    }
}
