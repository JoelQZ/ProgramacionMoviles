package com.tecsup.catalogo.ui.favoritos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.catalogo.ui.UiState
import com.tecsup.catalogo.ui.components.Cargando
import com.tecsup.catalogo.ui.components.ErrorView
import com.tecsup.catalogo.ui.lista.ItemFila

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    viewModel: FavoritosViewModel,
    onItemClick: (Int) -> Unit,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "♥ FAVORITOS CYBER",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Text("←", fontSize = 22.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            )
        }
    ) { padding ->
        val screenModifier = Modifier.padding(padding).fillMaxSize()
        when (val e = estado) {
            is UiState.Cargando -> Cargando(screenModifier)
            is UiState.Error -> ErrorView(mensaje = e.mensaje, onReintentar = {}, modifier = screenModifier)
            is UiState.Exito -> {
                if (e.datos.isEmpty()) {
                    Box(modifier = screenModifier, contentAlignment = Alignment.Center) {
                        Text(
                            text = "Aún no tienes favoritos guardados",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = screenModifier,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(e.datos, key = { it.id }) { item ->
                            ItemFila(
                                item = item,
                                onClick = { onItemClick(item.id) },
                                onFavorito = { viewModel.alternarFavorito(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
