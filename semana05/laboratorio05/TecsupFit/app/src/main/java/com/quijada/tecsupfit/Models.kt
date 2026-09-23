package com.quijada.tecsupfit

data class ClaseGimnasio(
    val id: Int,
    val nombre: String,
    val horario: String,
    val sala: String,
    val duracion: String,
    val descripcion: String,
    val cuposDisponibles: Int,
    val cuposTotales: Int,
    val categoria: String
)

data class HorarioDisponible(
    val id: Int,
    val hora: String,
    val sala: String,
    val disponible: Boolean
)

data class Reserva(
    val id: Int,
    val nombreClase: String,
    val fechaHora: String,
    val sala: String,
    val estado: String
)

object DatosEjemplo {
    val categoriasFiltro = listOf("Hoy", "Esta semana")

    val listaClases = listOf(
        ClaseGimnasio(
            id = 1,
            nombre = "Yoga funcional",
            horario = "7:00 am",
            sala = "Sala 2",
            duracion = "45 min",
            descripcion = "Mejora tu flexibilidad y fuerza postural con ejercicios guiados.",
            cuposDisponibles = 5,
            cuposTotales = 12,
            categoria = "Hoy"
        ),
        ClaseGimnasio(
            id = 2,
            nombre = "Cross Training",
            horario = "6:00 pm",
            sala = "Sala 1",
            duracion = "45 min",
            descripcion = "Entrenamiento funcional de alta intensidad. Cupos limitados.",
            cuposDisponibles = 8,
            cuposTotales = 12,
            categoria = "Hoy"
        ),
        ClaseGimnasio(
            id = 3,
            nombre = "Spinning",
            horario = "7:30 pm",
            sala = "Sala 3",
            duracion = "50 min",
            descripcion = "Sesion cardio de ciclismo de alta energia con musica motivadora.",
            cuposDisponibles = 2,
            cuposTotales = 15,
            categoria = "Esta semana"
        )
    )

    val horariosClase = listOf(
        HorarioDisponible(id = 1, hora = "7:00 am", sala = "Sala 2", disponible = true),
        HorarioDisponible(id = 2, hora = "6:00 pm", sala = "Sala 1", disponible = true),
        HorarioDisponible(id = 3, hora = "7:30 pm", sala = "Sala 3", disponible = true)
    )

    val listaReservas = listOf(
        Reserva(1, "Cross Training", "Hoy, 6:00 pm", "Sala 1", "Confirmada"),
        Reserva(2, "Yoga funcional", "Ayer, 7:00 am", "Sala 2", "Completada")
    )
}