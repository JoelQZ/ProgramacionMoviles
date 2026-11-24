package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onPostClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val estado by vm.lista.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val est = estado) {
            is UiState.Cargando -> {
                Cargando()
            }
            is UiState.Error -> {
                ErrorConReintento(
                    mensaje = est.mensaje,
                    onReintentar = { vm.cargar() }
                )
            }
            is UiState.Exito -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(est.datos) { post ->
                        TarjetaPost(
                            post = post,
                            onClick = { onPostClick(post.id) }
                        )
                    }
                }
            }
        }
    }
}
