package com.tecsup.catalogo

import android.content.Context
import com.tecsup.catalogo.data.auth.AuthRepository
import com.tecsup.catalogo.data.auth.FirebaseAuthRepository
import com.tecsup.catalogo.data.local.AppDatabase
import com.tecsup.catalogo.data.remote.RetrofitClient
import com.tecsup.catalogo.data.repository.ItemRepository
import com.tecsup.catalogo.data.repository.ItemRepositoryImpl
import com.tecsup.catalogo.notification.Notificador
import com.tecsup.catalogo.notification.NotificadorAndroid

class AppContainer(context: Context) {

    private val contextoApp = context.applicationContext

    private val baseDeDatos = AppDatabase.obtener(contextoApp)

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(RetrofitClient.api, baseDeDatos.itemDao())
    }

    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }

    val notificador: Notificador by lazy {
        NotificadorAndroid(contextoApp)
    }
}
