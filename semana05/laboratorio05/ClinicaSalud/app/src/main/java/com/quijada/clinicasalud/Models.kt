package com.quijada.clinicasalud

import androidx.compose.runtime.mutableStateListOf

data class Medico(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val calificacion: Double,
    val experiencia: String,
    val biografia: String
)

data class Cita(
    val id: Int,
    val medicoNombre: String,
    val especialidad: String,
    val fecha: String,
    val hora: String,
    var estado: String
)

object Repository {
    val medicos = listOf(
        Medico(1, "Dra. Ana Torres", "Cardiología", 4.9, "12 años exp.", "Especialista en arritmias e hipertensión. Formación en la Clínica Mayo."),
        Medico(2, "Dr. Luis Vega", "Pediatría", 4.7, "8 años exp.", "Especialista en pediatría general y desarrollo infantil."),
        Medico(3, "Dra. Rosa Díaz", "Dermatología", 4.8, "10 años exp.", "Experta en dermatología clínica y procedimientos estéticos.")
    )

    val citas = mutableStateListOf(
        Cita(1, "Dra. Ana Torres", "Cardiología", "Viernes 27", "10:30 am", "Confirmada"),
        Cita(2, "Dr. Luis Vega", "Pediatría", "Miércoles 15", "3:00 pm", "Completada")
    )
}