package com.quijada.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quijada.centrotareas.data.TareasRepository
import com.quijada.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class TareasViewModel(
    private val repository: TareasRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inactivo)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    private var jobTarea: Job? = null
    private var jobPrimos: Job? = null

    fun tareaLarga() {
        jobTarea?.cancel()
        jobTarea = viewModelScope.launch {
            _corriendo.value = true
            _progreso.value = 0f
            _mensaje.value = "Trabajando..."
            Log.d("HILO", "Antes: ${Thread.currentThread().name}")
            try {
                withContext(Dispatchers.Default) {
                    Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
                    for (i in 1..100) {
                        delay(40)
                        _progreso.value = i / 100f
                    }
                }
                Log.d("HILO", "Despues: ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } catch (e: CancellationException) {
                _progreso.value = 0f
                _mensaje.value = "Cancelado"
                throw e
            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelarTarea() {
        jobTarea?.cancel()
    }

    fun calcularPrimos(hasta: Int) {
        if (hasta < 2) return
        jobPrimos?.cancel()
        jobPrimos = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando
            try {
                var cantidad = 0
                val duracionMs = measureTimeMillis {
                    cantidad = repository.contarPrimos(hasta)
                }
                _primos.value = PrimosUi.Listo(
                    hasta = hasta,
                    cantidad = cantidad,
                    duracionMs = duracionMs
                )
            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw e
            }
        }
    }

    fun cancelarPrimos() {
        jobPrimos?.cancel()
    }
}
