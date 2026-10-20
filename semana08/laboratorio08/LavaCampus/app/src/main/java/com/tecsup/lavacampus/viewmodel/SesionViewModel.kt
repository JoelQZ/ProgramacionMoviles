package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repository: LavaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val perfilActivo = prefs.perfilActivo
    val modoOscuro = prefs.modoOscuro

    fun login(correo: String, clave: String, rolSeleccionado: Rol, onSuccess: (String) -> Unit) {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Por favor, completa todos los campos"
            return
        }

        viewModelScope.launch {
            val usuario = repository.login(correo.trim(), clave.trim())
            if (usuario == null) {
                _error.value = "Correo o contraseña incorrectos"
            } else if (usuario.rol != rolSeleccionado.name) {
                _error.value = "El usuario no corresponde al rol de ${rolSeleccionado.name.lowercase().capitalize()}"
            } else {
                _error.value = null
                _usuarioActual.value = usuario
                prefs.guardarSesion(usuario.rol, usuario.id)
                onSuccess(usuario.rol)
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            _usuarioActual.value = null
            prefs.cerrarSesion()
        }
    }
}