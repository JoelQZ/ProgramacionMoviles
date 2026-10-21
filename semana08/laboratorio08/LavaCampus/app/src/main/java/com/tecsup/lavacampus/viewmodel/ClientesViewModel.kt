package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.ClienteResumen
import kotlinx.coroutines.flow.*
import java.util.Locale

class ClientesViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    val textoBusqueda = MutableStateFlow("")

    val clientes: StateFlow<List<ClienteResumen>> = combine(
        repository.observarClientes(),
        textoBusqueda
    ) { lista, query ->
        if (query.isBlank()) {
            lista
        } else {
            val q = query.lowercase(Locale.getDefault())
            lista.filter {
                it.nombre.lowercase(Locale.getDefault()).contains(q) ||
                        it.codigo.lowercase(Locale.getDefault()).contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun buscar(query: String) {
        textoBusqueda.value = query
    }
}