package com.saludplus.citas.navigation

sealed class Rutas(val ruta: String) {
    object Splash : Rutas("splash")
    object Login : Rutas("login")
    object Registro : Rutas("registro")
    object Terminos : Rutas("terminos")
    object Home : Rutas("home")
    object Especialidades : Rutas("especialidades")

    object Medicos : Rutas("medicos/{especialidadId}") {
        fun crearRuta(especialidadId: String) = "medicos/$especialidadId"
    }

    object FechaHora : Rutas("fecha_hora/{medicoId}") {
        fun crearRuta(medicoId: String) = "fecha_hora/$medicoId"
    }

    object ConfirmarCita : Rutas("confirmar_cita/{medicoId}/{fecha}/{hora}") {
        fun crearRuta(medicoId: String, fecha: String, hora: String): String {
            val fechaLimpia = fecha.replace("/", "-")
            val horaLimpia = hora.replace(" ", "")
            return "confirmar_cita/$medicoId/$fechaLimpia/$horaLimpia"
        }
    }

    object CitaExitosa : Rutas("cita_exitosa")
    object MisCitas : Rutas("mis_citas")

    object DetalleCita : Rutas("detalle_cita/{citaId}") {
        fun crearRuta(citaId: String) = "detalle_cita/$citaId"
    }

    object Perfil : Rutas("perfil")
    object Resultados : Rutas("resultados")
    object Notificaciones : Rutas("notificaciones")
}