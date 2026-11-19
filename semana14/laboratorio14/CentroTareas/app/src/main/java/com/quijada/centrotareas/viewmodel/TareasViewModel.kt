package com.quijada.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quijada.centrotareas.data.TareasRepository
import com.quijada.centrotareas.model.CargaUi
import com.quijada.centrotareas.model.ComparacionFila
import com.quijada.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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

    private val _progresoPrimos = MutableStateFlow(0f)
    val progresoPrimos: StateFlow<Float> = _progresoPrimos.asStateFlow()

    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Idle)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    private val _limitarTiempo = MutableStateFlow(false)
    val limitarTiempo: StateFlow<Boolean> = _limitarTiempo.asStateFlow()

    private val _tablaComparacion = MutableStateFlow<List<ComparacionFila>>(emptyList())
    val tablaComparacion: StateFlow<List<ComparacionFila>> = _tablaComparacion.asStateFlow()

    val historial: StateFlow<List<String>> = repository.historialResultados
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var jobTarea: Job? = null
    private var jobPrimos: Job? = null

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    fun cambiarLimitarTiempo(valor: Boolean) {
        _limitarTiempo.value = valor
    }

    fun limpiarHistorial() {
        viewModelScope.launch {
            repository.limpiarHistorial()
        }
    }

    fun limpiarComparacion() {
        _tablaComparacion.value = emptyList()
    }

    private fun registrarTiempoSecuencial(ms: Long) {
        val lista = _tablaComparacion.value.toMutableList()
        val ultima = lista.lastOrNull()
        if (ultima == null || ultima.tiempoSecuencialMs != null) {
            lista.add(ComparacionFila(corrida = lista.size + 1, tiempoSecuencialMs = ms))
        } else {
            lista[lista.lastIndex] = ultima.copy(tiempoSecuencialMs = ms)
        }
        _tablaComparacion.value = lista
    }

    private fun registrarTiempoConcurrente(ms: Long) {
        val lista = _tablaComparacion.value.toMutableList()
        val ultima = lista.lastOrNull()
        if (ultima == null || ultima.tiempoConcurrenteMs != null) {
            lista.add(ComparacionFila(corrida = lista.size + 1, tiempoConcurrenteMs = ms))
        } else {
            lista[lista.lastIndex] = ultima.copy(tiempoConcurrenteMs = ms)
        }
        _tablaComparacion.value = lista
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
        _progresoPrimos.value = 0f
        jobPrimos = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando
            try {
                var cantidad = 0
                val duracionMs = measureTimeMillis {
                    cantidad = repository.contarPrimos(hasta) { progreso ->
                        _progresoPrimos.value = progreso
                    }
                }
                _primos.value = PrimosUi.Listo(
                    hasta = hasta,
                    cantidad = cantidad,
                    duracionMs = duracionMs
                )
                repository.guardarResultado("Primos hasta $hasta: $cantidad ($duracionMs ms)")
            } catch (e: CancellationException) {
                _progresoPrimos.value = 0f
                _primos.value = PrimosUi.Cancelado
                throw e
            }
        }
    }

    fun cancelarPrimos() {
        jobPrimos?.cancel()
        _progresoPrimos.value = 0f
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
                registrarTiempoSecuencial(ms)
                repository.guardarResultado("Carga Una por una: $ms ms")
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
                registrarTiempoConcurrente(ms)
                repository.guardarResultado("Carga A la vez: $ms ms")
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
