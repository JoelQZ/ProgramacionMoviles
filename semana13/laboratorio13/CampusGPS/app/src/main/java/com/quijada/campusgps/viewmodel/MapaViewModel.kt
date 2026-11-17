package com.quijada.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quijada.campusgps.data.LugaresRepository
import com.quijada.campusgps.data.UbicacionRepository
import com.quijada.campusgps.model.UbicacionUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    fun buscar() {
        viewModelScope.launch {
            _estado.value = UbicacionUi.Buscando
            val pos = ubicacionRepo.obtenerUbicacionActual()
            if (pos != null) {
                _estado.value = UbicacionUi.Lista(pos)
            } else {
                _estado.value = UbicacionUi.Error("No se pudo obtener la ubicación")
            }
        }
    }
}
