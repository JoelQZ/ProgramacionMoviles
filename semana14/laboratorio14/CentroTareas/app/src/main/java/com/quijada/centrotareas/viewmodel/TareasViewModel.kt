package com.quijada.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import com.quijada.centrotareas.data.TareasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TareasViewModel(
    private val repository: TareasRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    fun tareaLargaMala() {
        _mensaje.value = "Trabajando..."
        Thread.sleep(4000)
        _mensaje.value = "Terminó"
    }
}
