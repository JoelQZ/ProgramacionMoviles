package com.tecsup.catalogo.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tecsup.catalogo.AppContainer
import com.tecsup.catalogo.data.repository.ItemRepository
import com.tecsup.catalogo.domain.model.Item
import com.tecsup.catalogo.notification.Notificador
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ListaViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    private var refrescoTerminado = false

    init {
        viewModelScope.launch {
            repo.items.collect { lista ->
                if (lista.isNotEmpty() || refrescoTerminado) _estado.value = UiState.Exito(lista)
            }
        }
        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {
            if (repo.cantidadGuardada() == 0) _estado.value = UiState.Cargando
            val resultado = repo.refrescar()
            refrescoTerminado = true
            resultado
                .onSuccess { n ->
                    if (_estado.value is UiState.Cargando) _estado.value = UiState.Exito(emptyList())
                    _aviso.value = null
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }
                .onFailure { e ->
                    if (repo.cantidadGuardada() == 0) {
                        _estado.value = UiState.Error(mensajeDe(e))
                    } else {
                        _aviso.value = "Sin conexión: mostrando datos guardados"
                    }
                }
        }
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }

    fun toggleFavorito(id: Int) {
        alternarFavorito(id)
    }

    fun toggleFavorito(item: Item) {
        alternarFavorito(item.id)
    }

    private fun mensajeDe(e: Throwable) = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        else -> "Ocurrió un error inesperado"
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { ListaViewModel(container.itemRepository, container.notificador) }
        }
    }
}
