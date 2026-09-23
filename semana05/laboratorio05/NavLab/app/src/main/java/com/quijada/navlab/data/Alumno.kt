package com.quijada.navlab.data

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
        biografia = "Estudiante de 4to ciclo enfocado en desarrollo de aplicaciones móviles nativas con Kotlin y Jetpack Compose. Apasionado por la arquitectura de software y el diseño UI/UX."
    ),
    Alumno(
        id = 2,
        nombre = "María García",
        carrera = "Redes y Comunicaciones",
        correo = "maria.garcia@tecsup.edu.pe",
        facultad = "Ingeniería y Tecnologías",
        biografia = "Especializándose en ciberseguridad, infraestructura de redes y servicios en la nube. Participante activa en comunidades de tecnología."
    ),
    Alumno(
        id = 3,
        nombre = "Carlos Pérez",
        carrera = "Diseño y Desarrollo de Software",
        correo = "carlos.perez@tecsup.edu.pe",
        facultad = "Tecnología Digital",
        biografia = "Desarrollador Jr. orientado a backend, bases de datos e integración de microservicios. Entusiasta del código limpio y patrones de diseño."
    )
)
