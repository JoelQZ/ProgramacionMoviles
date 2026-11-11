package com.tecsup.catalogo.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tecsup.catalogo.AppContainer
import com.tecsup.catalogo.data.repository.ItemRepository
import com.tecsup.catalogo.notification.Notificador
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class Totales(val guardados: Int, val favoritos: Int)

class AdminViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    val estado: StateFlow<UiState<Totales>> = combine(repo.items, repo.favoritos) { items, favoritos ->
        val totales: UiState<Totales> = UiState.Exito(Totales(items.size, favoritos.size))
        totales
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Cargando)

    private val _sincronizando = MutableStateFlow(false)
    val sincronizando: StateFlow<Boolean> = _sincronizando.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun sincronizar() {
        viewModelScope.launch {
            _sincronizando.value = true
            repo.refrescar()
                .onSuccess { n ->
                    _mensaje.value = "Sincronizado: se guardaron $n elementos"
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }
                .onFailure { _mensaje.value = "No se pudo sincronizar. Revisa tu conexión" }
            _sincronizando.value = false
        }
    }

    fun limpiarCache() {
        viewModelScope.launch {
            repo.limpiarCache()
            _mensaje.value = "Caché limpiada"
        }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { AdminViewModel(container.itemRepository, container.notificador) }
        }
    }
}
