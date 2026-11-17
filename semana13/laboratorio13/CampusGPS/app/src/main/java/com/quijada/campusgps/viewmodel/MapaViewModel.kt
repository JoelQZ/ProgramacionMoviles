package com.quijada.campusgps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quijada.campusgps.data.LugaresRepository
import com.quijada.campusgps.data.PreferenciasRepository
import com.quijada.campusgps.data.UbicacionRepository
import com.quijada.campusgps.model.Lugar
import com.quijada.campusgps.model.LugarConDistancia
import com.quijada.campusgps.model.Posicion
import com.quijada.campusgps.model.TipoError
import com.quijada.campusgps.model.UbicacionUi
import com.quijada.campusgps.util.distanciaMetros
import com.quijada.campusgps.util.formatearDistancia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository,
    private val preferenciasRepo: PreferenciasRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _baseLugares = MutableStateFlow<List<LugarConDistancia>>(emptyList())

    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
    val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada.asStateFlow()

    private val _radioSeleccionado = MutableStateFlow<Int?>(null)
    val radioSeleccionado: StateFlow<Int?> = _radioSeleccionado.asStateFlow()

    val lugares: StateFlow<List<LugarConDistancia>> = combine(
        _baseLugares,
        _categoriaSeleccionada,
        _radioSeleccionado
    ) { lista, cat, radio ->
        lista
            .filter { item ->
                if (cat.isNullOrEmpty() || cat == "Todos") true
                else item.lugar.categoria.equals(cat, ignoreCase = true)
            }
            .map { item ->
                val enRadio = (radio != null && item.metros != null && item.metros <= radio)
                item.copy(dentroDelRadio = enRadio)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    val masCercano: StateFlow<Lugar?> = lugares
        .map { lista -> lista.firstOrNull { it.metros != null }?.lugar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val ultimoLugarGuardadoId: StateFlow<Int?> = preferenciasRepo.ultimoLugarId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        _baseLugares.value = lugaresRepo.obtenerLugares().map { LugarConDistancia(lugar = it, metros = null) }

        viewModelScope.launch {
            preferenciasRepo.ultimoLugarId.collect { id ->
                if (id != null && _seleccionado.value == null) {
                    val existe = _baseLugares.value.any { it.lugar.id == id }
                    if (existe) {
                        _seleccionado.value = id
                    }
                }
            }
        }
    }

    fun buscar() {
        viewModelScope.launch {
            _estado.value = UbicacionUi.Buscando

            if (!ubicacionRepo.playServicesDisponible()) {
                _estado.value = UbicacionUi.Error(
                    mensaje = "Este dispositivo no tiene Google Play Services",
                    tipo = TipoError.SIN_PLAY_SERVICES
                )
                return@launch
            }

            val pos = ubicacionRepo.obtenerUbicacionActual()
            if (pos != null) {
                actualizarDistancias(pos)
                _estado.value = UbicacionUi.Lista(pos)
            } else {
                _estado.value = UbicacionUi.Error(
                    mensaje = "No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo",
                    tipo = TipoError.GPS_APAGADO
                )
            }
        }
    }

    private fun actualizarDistancias(pos: Posicion) {
        val listaConDistancias = _baseLugares.value.map { item ->
            val d = distanciaMetros(pos, item.lugar)
            LugarConDistancia(lugar = item.lugar, metros = d)
        }.sortedBy { it.metros }

        _baseLugares.value = listaConDistancias

        listaConDistancias.forEach { item ->
            val dStr = item.metros?.let { formatearDistancia(it) } ?: "Sin calcular"
            Log.d("CampusGPS_Distancia", "${item.lugar.nombre}: $dStr")
        }
    }

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
        if (id != null) {
            viewModelScope.launch {
                preferenciasRepo.guardarUltimoLugar(id)
            }
        }
    }

    fun seleccionarCategoria(categoria: String?) {
        _categoriaSeleccionada.value = if (categoria == "Todos") null else categoria
    }

    fun seleccionarRadio(radioMetros: Int?) {
        _radioSeleccionado.value = radioMetros
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            mensaje = "Sin permiso de ubicación no puedo mostrar dónde estás",
            tipo = TipoError.SIN_PERMISO
        )
    }
}
