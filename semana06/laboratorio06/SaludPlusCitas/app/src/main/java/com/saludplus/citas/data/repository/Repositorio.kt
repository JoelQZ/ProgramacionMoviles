package com.saludplus.citas.data.repository

import com.saludplus.citas.data.model.Cita
import com.saludplus.citas.data.model.Especialidad
import com.saludplus.citas.data.model.Medico
import com.saludplus.citas.data.model.Usuario

object Repositorio {

    private val usuarios = mutableListOf<Usuario>()
    var usuarioActual: Usuario? = null
        private set

    private val citas = mutableListOf<Cita>()

    val especialidades = listOf(
        Especialidad("1", "Medicina General", "Atención médica primaria y preventiva.", "stethoscope"),
        Especialidad("2", "Pediatría", "Atención para bebés, niños y adolescentes.", "child_care"),
        Especialidad("3", "Cardiología", "Especialistas en salud del corazón.", "favorite"),
        Especialidad("4", "Dermatología", "Cuidado de la piel, cabello y uñas.", "face")
    )

    val medicos = listOf(
        Medico("m1", "1", "Dr. Carlos Mendoza", "Medicina General", "CMP-12345", 4.8, 120),
        Medico("m2", "1", "Dra. Ana Torres", "Medicina General", "CMP-23456", 4.7, 95),
        Medico("m3", "2", "Dr. Luis Paredes", "Pediatría", "CMP-34567", 4.9, 150),
        Medico("m4", "3", "Dra. Elena Gómez", "Cardiología", "CMP-45678", 4.9, 210),
        Medico("m5", "4", "Dr. Roberto Silva", "Dermatología", "CMP-56789", 4.6, 80)
    )

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

    fun agregarCita(cita: Cita) {
        citas.add(cita)
    }

    fun obtenerCitasUsuario(): List<Cita> {
        return citas.filter { it.usuarioEmail.equals(usuarioActual?.email, ignoreCase = true) }
    }

    fun obtenerMedicoPorId(id: String): Medico? {
        return medicos.find { it.id == id }
    }

    fun obtenerEspecialidadPorId(id: String): Especialidad? {
        return especialidades.find { it.id == id }
    }
}