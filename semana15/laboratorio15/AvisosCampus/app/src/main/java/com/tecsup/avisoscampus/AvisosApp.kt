package com.tecsup.avisoscampus

import android.app.Application
import com.tecsup.avisoscampus.notificacion.Notificador

class AvisosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Notificador.crearCanal(this)
    }
}
