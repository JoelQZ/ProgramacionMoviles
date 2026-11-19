package com.quijada.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quijada.centrotareas.data.TareasRepository
import com.quijada.centrotareas.model.CargaUi
import com.quijada.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.coroutineContext
import kotlin.system.measureTimeMillis

class TareasViewModel(
    private val repository: TareasRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    private val _enProgreso = MutableStateFlow(false)
    val enProgreso: StateFlow<Boolean> = _enProgreso.asStateFlow()

    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inactivo)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Idle)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    private val _limitarTiempo = MutableStateFlow(false)
    val limitarTiempo: StateFlow<Boolean> = _limitarTiempo.asStateFlow()

    private var jobTarea: Job? = null
    private var jobPrimos: Job? = null

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    fun cambiarLimitarTiempo(valor: Boolean) {
        _limitarTiempo.value = valor
    }

    fun tareaLarga() {
        jobTarea?.cancel()
        jobTarea = viewModelScope.launch {
            val miJob = coroutineContext[Job]
            _enProgreso.value = true
            _progreso.value = 0f
            _mensaje.value = "Trabajando..."
            Log.d("HILO", "Antes: ${Thread.currentThread().name}")
            try {
                repository.ejecutarTareaConProgreso().collect { valor ->
                    _progreso.value = valor
                }
                Log.d("HILO", "Despues: ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } catch (e: CancellationException) {
                _progreso.value = 0f
                _mensaje.value = "Cancelado"
                throw e
            } finally {
                if (jobTarea == miJob) {
                    _enProgreso.value = false
                }
            }
        }
    }

    fun cancelarTarea() {
        jobTarea?.cancel()
        _progreso.value = 0f
        _mensaje.value = "Cancelado"
        _enProgreso.value = false
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

    fun cargarUnaPorUna() {
        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            try {
                var perfil = ""
                var pedidos = emptyList<String>()
                val ms = measureTimeMillis {
                    if (_limitarTiempo.value) {
                        withTimeout(2000L) {
                            perfil = repository.cargarPerfil()
                            pedidos = repository.cargarPedidos(_simularError.value)
                        }
                    } else {
                        perfil = repository.cargarPerfil()
                        pedidos = repository.cargarPedidos(_simularError.value)
                    }
                }
                _carga.value = CargaUi.Exito(
                    modo = "Una por una",
                    perfil = perfil,
                    cantidadPedidos = pedidos.size,
                    duracionMs = ms
                )
            } catch (e: TimeoutCancellationException) {
                _carga.value = CargaUi.Error("Tardó demasiado")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: ${e.message}")
            }
        }
    }

    fun cargarALaVez() {
        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            try {
                var perfil = ""
                var pedidos = emptyList<String>()
                val ms = measureTimeMillis {
                    if (_limitarTiempo.value) {
                        withTimeout(2000L) {
                            val (p, ped) = coroutineScope {
                                val a = async { repository.cargarPerfil() }
                                val b = async { repository.cargarPedidos(_simularError.value) }
                                Pair(a.await(), b.await())
                            }
                            perfil = p
                            pedidos = ped
                        }
                    } else {
                        val (p, ped) = coroutineScope {
                            val a = async { repository.cargarPerfil() }
                            val b = async { repository.cargarPedidos(_simularError.value) }
                            Pair(a.await(), b.await())
                        }
                        perfil = p
                        pedidos = ped
                    }
                }
                _carga.value = CargaUi.Exito(
                    modo = "A la vez",
                    perfil = perfil,
                    cantidadPedidos = pedidos.size,
                    duracionMs = ms
                )
            } catch (e: TimeoutCancellationException) {
                _carga.value = CargaUi.Error("Tardó demasiado")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: ${e.message}")
            }
        }
    }
}
