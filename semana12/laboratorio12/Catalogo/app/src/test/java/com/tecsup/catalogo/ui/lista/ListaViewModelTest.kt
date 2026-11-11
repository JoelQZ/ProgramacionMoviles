package com.tecsup.catalogo.ui.lista

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tecsup.catalogo.MainDispatcherRule
import com.tecsup.catalogo.data.repository.ItemRepository
import com.tecsup.catalogo.domain.model.Item
import com.tecsup.catalogo.notification.Notificador
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class FakeItemRepository(
    var initialItems: List<Item> = emptyList(),
    var cantidadGuardadaValue: Int = 0,
    var refrescarResult: Result<Int> = Result.success(0)
) : ItemRepository {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    override val items: Flow<List<Item>> = _items

    init {
        _items.value = initialItems
    }

    private val _favoritos = MutableStateFlow(initialItems.filter { it.esFavorito })
    override val favoritos: Flow<List<Item>> = _favoritos

    var alternarFavoritoCalledWithId: Int? = null

    override suspend fun refrescar(): Result<Int> {
        if (refrescarResult.isSuccess) {
            _items.value = initialItems
        }
        return refrescarResult
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> {
        val item = initialItems.find { it.id == id }
        return if (item != null) Result.success(item) else Result.failure(Exception("Not found"))
    }

    override suspend fun alternarFavorito(id: Int) {
        alternarFavoritoCalledWithId = id
    }

    override suspend fun cantidadGuardada(): Int = cantidadGuardadaValue

    override suspend fun cantidadFavoritos(): Int = initialItems.count { it.esFavorito }

    override suspend fun limpiarCache() {
        initialItems = emptyList()
        _items.value = emptyList()
    }
}

class FakeNotificador : Notificador {
    var ultimoTitulo: String? = null
    var ultimoTexto: String? = null

    override fun mostrar(titulo: String, texto: String) {
        ultimoTitulo = titulo
        ultimoTexto = texto
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sampleItems = listOf(
        Item(id = 1, titulo = "Laptop", subtitulo = "Tecnología", imagenUrl = "", esFavorito = false)
    )

    @Test
    fun cargarItems_exito() = runTest {
        // Given
        val fakeRepo = FakeItemRepository(
            initialItems = sampleItems,
            cantidadGuardadaValue = 1,
            refrescarResult = Result.success(1)
        )
        val fakeNotificador = FakeNotificador()

        // When
        val viewModel = ListaViewModel(fakeRepo, fakeNotificador)

        // Then
        val state = viewModel.estado.value
        assertTrue(state is UiState.Exito)
        assertEquals(sampleItems, (state as UiState.Exito).datos)
    }

    @Test
    fun cargarItems_error() = runTest {
        // Given
        val fakeRepo = FakeItemRepository(
            initialItems = emptyList(),
            cantidadGuardadaValue = 0,
            refrescarResult = Result.failure(IOException())
        )
        val fakeNotificador = FakeNotificador()

        // When
        val viewModel = ListaViewModel(fakeRepo, fakeNotificador)

        // Then
        val state = viewModel.estado.value
        assertTrue(state is UiState.Error)
        assertEquals("No hay conexión a internet", (state as UiState.Error).mensaje)
    }

    @Test
    fun toggleFavorito_llamaRepository() = runTest {
        // Given
        val fakeRepo = FakeItemRepository(
            initialItems = sampleItems,
            cantidadGuardadaValue = 1,
            refrescarResult = Result.success(1)
        )
        val fakeNotificador = FakeNotificador()
        val viewModel = ListaViewModel(fakeRepo, fakeNotificador)

        // When
        val item = sampleItems[0]
        viewModel.toggleFavorito(item)

        // Then
        assertEquals(1, fakeRepo.alternarFavoritoCalledWithId)
    }
}
