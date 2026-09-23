package com.quijada.navlab.model

data class Alumno(
    val id: Int,
    val nombre: String,
    val carrera: String,
    val correo: String,
    val facultad: String,
    val biografia: String
)

val listaAlumnos = listOf(
    Alumno(
        id = 1,
        nombre = "Juan León",
        carrera = "Diseño y Desarrollo de Software",
        correo = "juan.leon@tecsup.edu.pe",
        facultad = "Tecnología Digital",
        biografia = "Estudiante apasionado por el desarrollo móvil Android con Jetpack Compose y la arquitectura de software. Actualmente cursa el 4to ciclo."
    ),
    Alumno(
        id = 2,
        nombre = "María García",
        carrera = "Redes y Comunicaciones",
        correo = "maria.garcia@tecsup.edu.pe",
        facultad = "Tecnología Digital",
        biografia = "Estudiante enfocada en ciberseguridad, infraestructura en la nube y administración de redes avanzadas. Cursa el 5to ciclo."
    ),
    Alumno(
        id = 3,
        nombre = "Carlos Pérez",
        carrera = "Diseño y Desarrollo de Software",
        correo = "carlos.perez@tecsup.edu.pe",
        facultad = "Tecnología Digital",
        biografia = "Entusiasta de la inteligencia artificial, ciencia de datos y desarrollo backend con Kotlin y Java. Cursa el 3er ciclo."
    )
)
