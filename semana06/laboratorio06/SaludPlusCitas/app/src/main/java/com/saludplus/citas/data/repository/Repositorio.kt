package com.saludplus.citas.data.repository

import com.saludplus.citas.data.model.Usuario

object Repositorio {

    private val usuarios = mutableListOf<Usuario>()
    var usuarioActual: Usuario? = null
        private set

    fun registrarUsuario(usuario: Usuario): Boolean {
        if (usuarios.any { it.email.equals(usuario.email, ignoreCase = true) }) {
            return false
        }
        usuarios.add(usuario)
        usuarioActual = usuario
        return true
    }

    fun iniciarSesion(email: String, clave: String): Usuario? {
        val usuario = usuarios.find {
            it.email.equals(email, ignoreCase = true) && it.clave == clave
        }
        if (usuario != null) {
            usuarioActual = usuario
        }
        return usuario
    }

    fun cerrarSesion() {
        usuarioActual = null
    }
}