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
        Medico(
            id = "m1",
            especialidadId = "1",
            nombre = "Dr. Carlos Mendoza",
            especialidadNombre = "Medicina General",
            cmp = "CMP-12345",
            sede = "Sede Central - Av. Los Olivos 123",
            telefono = "+51 987 654 321",
            calificacion = 4.8,
            resenas = 120
        ),
        Medico(
            id = "m2",
            especialidadId = "1",
            nombre = "Dra. Ana Torres",
            especialidadNombre = "Medicina General",
            cmp = "CMP-23456",
            sede = "Sede Norte - Av. Las Flores 456",
            telefono = "+51 912 345 678",
            calificacion = 4.7,
            resenas = 95
        ),
        Medico(
            id = "m3",
            especialidadId = "2",
            nombre = "Dr. Luis Paredes",
            especialidadNombre = "Pediatría",
            cmp = "CMP-34567",
            sede = "Sede Sur - Av. Arequipa 789",
            telefono = "+51 923 456 789",
            calificacion = 4.9,
            resenas = 150
        ),
        Medico(
            id = "m4",
            especialidadId = "3",
            nombre = "Dra. Elena Gómez",
            especialidadNombre = "Cardiología",
            cmp = "CMP-45678",
            sede = "Sede Central - Av. Los Olivos 123",
            telefono = "+51 934 567 890",
            calificacion = 4.9,
            resenas = 210
        ),
        Medico(
            id = "m5",
            especialidadId = "4",
            nombre = "Dr. Roberto Silva",
            especialidadNombre = "Dermatología",
            cmp = "CMP-56789",
            sede = "Sede Este - Av. Marina 321",
            telefono = "+51 945 678 901",
            calificacion = 4.6,
            resenas = 80
        )
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
    data class Sede(
        val id: String,
        val nombre: String,
        val direccion: String,
        val telefono: String
    )

    val sedes = listOf(
        Sede("s1", "Sede Central", "Av. Los Olivos 123, Lima", "+51 1 234 5678"),
        Sede("s2", "Sede Norte", "Av. Las Flores 456, Lima", "+51 1 345 6789"),
        Sede("s3", "Sede Sur", "Av. Arequipa 789, Lima", "+51 1 456 7890"),
        Sede("s4", "Sede Este", "Av. Marina 321, Lima", "+51 1 567 8901")
    )
}