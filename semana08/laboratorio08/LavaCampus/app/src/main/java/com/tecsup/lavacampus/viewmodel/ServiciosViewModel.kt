package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ServiciosViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    val serviciosActivos: StateFlow<List<Servicio>> = repository.observarServiciosActivos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todosLosServicios: StateFlow<List<Servicio>> = repository.observarTodosLosServicios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val mensajeError = MutableStateFlow<String?>(null)

    fun guardarNuevoServicio(
        nombre: String,
        descripcion: String,
        precio: Double,
        unidad: String,
        activo: Boolean
    ) {
        viewModelScope.launch {
            val nuevoServicio = Servicio(
                nombre = nombre,
                descripcion = descripcion,
                precio = precio,
                unidad = unidad,
                activo = activo
            )
            repository.insertarServicio(nuevoServicio)
        }
    }

    fun cambiarEstadoActivo(servicio: Servicio, activo: Boolean) {
        viewModelScope.launch {
            val actualizado = servicio.copy(activo = activo)
            repository.actualizarServicio(actualizado)
        }
    }

    fun eliminarOSidesactivar(servicio: Servicio) {
        viewModelScope.launch {
            val tienePedidos = repository.servicioTieneDetalles(servicio.id)
            if (tienePedidos) {
                mensajeError.value = "No se puede eliminar porque está en uso. Se ha desactivado."
                cambiarEstadoActivo(servicio, false)
            } else {
                repository.eliminarServicio(servicio)
            }
        }
    }

    fun limpiarMensaje() {
        mensajeError.value = null
    }

    fun guardarNuevoOSistemaServicio(
        id: Int?,
        nombre: String,
        descripcion: String,
        precio: Double,
        unidad: String,
        activo: Boolean
    ) {
        viewModelScope.launch {
            val servicio = Servicio(
                id = id ?: 0,
                nombre = nombre,
                descripcion = descripcion,
                precio = precio,
                unidad = unidad,
                activo = activo
            )
            if (id != null && id > 0) {
                repository.actualizarServicio(servicio)
            } else {
                repository.insertarServicio(servicio)
            }
        }
    }
}