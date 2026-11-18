package com.quijada.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quijada.centrotareas.data.TareasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareasViewModel(
    private val repository: TareasRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    fun tareaLarga() {
        viewModelScope.launch {
            _mensaje.value = "Trabajando..."
            Log.d("HILO", "Antes: ${Thread.currentThread().name}")
            withContext(Dispatchers.Default) {
                Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
                delay(4000)
            }
            Log.d("HILO", "Despues: ${Thread.currentThread().name}")
            _mensaje.value = "Terminó"
        }
    }
}
