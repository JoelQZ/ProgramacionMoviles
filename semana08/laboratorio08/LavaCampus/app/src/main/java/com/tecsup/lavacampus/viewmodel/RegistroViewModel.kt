package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    val nombre = MutableStateFlow("")
    val correo = MutableStateFlow("")
    val clave = MutableStateFlow("")
    val confirmarClave = MutableStateFlow("")

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    fun precargarPrueba() {
        nombre.value = "Joel Quijada"
        correo.value = "joel.quijada@tecsup.edu.pe"
        clave.value = "123456"
        confirmarClave.value = "123456"
        _error.value = null
    }

    fun registrar(onExito: (Usuario) -> Unit) {
        val nom = nombre.value.trim()
        val mail = correo.value.trim()
        val pass = clave.value.trim()
        val confirmPass = confirmarClave.value.trim()

        if (nom.isBlank() || mail.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            _error.value = "Por favor, completa todos los campos"
            return
        }

        if (!mail.contains("@")) {
            _error.value = "Ingresa un correo electrónico válido"
            return
        }

        if (pass != confirmPass) {
            _error.value = "Las contraseñas no coinciden"
            return
        }

        if (pass.length < 4) {
            _error.value = "La contraseña debe tener al menos 4 caracteres"
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            try {
                val existe = repository.obtenerUsuarioPorCorreo(mail)
                if (existe != null) {
                    _error.value = "El correo ya se encuentra registrado"
                    _cargando.value = false
                    return@launch
                }

                val nuevoUsuario = Usuario(
                    nombre = nom,
                    codigo = "20241001",
                    correo = mail,
                    clave = pass,
                    rol = "CLIENTE"
                )

                val idRegistrado = repository.registrarUsuario(nuevoUsuario)
                val usuarioCreado = nuevoUsuario.copy(id = idRegistrado.toInt())

                _error.value = null
                _cargando.value = false
                onExito(usuarioCreado)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Error al registrar: ${e.localizedMessage}"
                _cargando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}