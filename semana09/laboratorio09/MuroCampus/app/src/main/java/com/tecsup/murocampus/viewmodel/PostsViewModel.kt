package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.murocampus.data.PostRepository
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetalleUi(
    val post: Post,
    val comentarios: List<Comentario>
)

class PostsViewModel(private val repo: PostRepository) : ViewModel() {
    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista: StateFlow<UiState<List<Post>>> = _lista.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val listaFiltrada: StateFlow<UiState<List<Post>>> = combine(_lista, _busqueda) { state, query ->
        when (state) {
            is UiState.Exito -> {
                val q = query.trim()
                if (q.isEmpty()) {
                    state
                } else {
                    val filtrados = state.datos.filter {
                        it.title.contains(q, ignoreCase = true)
                    }
                    UiState.Exito(filtrados)
                }
            }
            else -> state
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Cargando
    )

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    private val _creacion = MutableStateFlow<UiState<Post>?>(null)
    val creacion: StateFlow<UiState<Post>?> = _creacion.asStateFlow()

    private val _eliminacion = MutableStateFlow<UiState<Unit>?>(null)
    val eliminacion: StateFlow<UiState<Unit>?> = _eliminacion.asStateFlow()

    init {
        cargar()
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            val userId = _usuarioFiltro.value
            val resultado = if (userId == null) {
                repo.listar()
            } else {
                repo.porUsuario(userId)
            }
            _lista.value = resultado.fold(
                onSuccess = { UiState.Exito(it) },
                onFailure = { UiState.Error(mensajeDe(it)) }
            )
        }
    }

    fun abrir(id: Int) {
        viewModelScope.launch {
            _detalle.value = UiState.Cargando
            try {
                coroutineScope {
                    val postDeferred = async { repo.detalle(id) }
                    val comentariosDeferred = async { repo.comentarios(id) }

                    val postResult = postDeferred.await()
                    val comentariosResult = comentariosDeferred.await()

                    if (postResult.isSuccess && comentariosResult.isSuccess) {
                        _detalle.value = UiState.Exito(
                            DetalleUi(postResult.getOrThrow(), comentariosResult.getOrThrow())
                        )
                    } else {
                        val error = postResult.exceptionOrNull() ?: comentariosResult.exceptionOrNull() ?: Exception("Error desconocido")
                        _detalle.value = UiState.Error(mensajeDe(error))
                    }
                }
            } catch (e: Exception) {
                _detalle.value = UiState.Error(mensajeDe(e))
            }
        }
    }

    fun publicar(titulo: String, texto: String) {
        viewModelScope.launch {
            _creacion.value = UiState.Cargando
            val nuevo = NuevoPost(userId = 1, title = titulo, body = texto)
            val resultado = repo.crear(nuevo)
            _creacion.value = resultado.fold(
                onSuccess = { UiState.Exito(it) },
                onFailure = { UiState.Error(mensajeDe(it)) }
            )
        }
    }

    fun limpiarCreacion() {
        _creacion.value = null
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            _eliminacion.value = UiState.Cargando
            val resultado = repo.eliminar(id)
            resultado.fold(
                onSuccess = {
                    _eliminacion.value = UiState.Exito(Unit)
                    val currentList = _lista.value
                    if (currentList is UiState.Exito) {
                        _lista.value = UiState.Exito(currentList.datos.filter { it.id != id })
                    }
                },
                onFailure = {
                    _eliminacion.value = UiState.Error(mensajeDe(it))
                }
            )
        }
    }

    fun limpiarEliminacion() {
        _eliminacion.value = null
    }
}
