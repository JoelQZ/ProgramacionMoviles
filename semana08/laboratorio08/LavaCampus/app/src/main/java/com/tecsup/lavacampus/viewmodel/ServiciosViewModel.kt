package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ServiciosViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    val serviciosActivos: StateFlow<List<Servicio>> = repository.observarServiciosActivos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}