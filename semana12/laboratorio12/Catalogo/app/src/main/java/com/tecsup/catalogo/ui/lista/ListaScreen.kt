package com.tecsup.catalogo.ui.lista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.catalogo.domain.model.Item
import com.tecsup.catalogo.ui.UiState
import com.tecsup.catalogo.ui.components.BotonFavorito
import com.tecsup.catalogo.ui.components.Cargando
import com.tecsup.catalogo.ui.components.ErrorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onItemClick: (Int) -> Unit,
    onVerFavoritos: () -> Unit,
    onCerrarSesion: () -> Unit,
    esAdmin: Boolean,
    onAbrirAdmin: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()
    val queryBusqueda by viewModel.queryBusqueda.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚡ CYBER_SMARTPHONES",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                actions = {
                    TextButton(onClick = onVerFavoritos) {
                        Text("♥ Favoritos", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                    }
                    if (esAdmin) {
                        TextButton(onClick = onAbrirAdmin) {
                            Text("⚙ Admin", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        }
                    }
                    TextButton(onClick = onCerrarSesion) {
                        Text("Salir", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (val e = estado) {
            is UiState.Cargando -> Cargando(modifier)
            is UiState.Error -> ErrorView(
                mensaje = e.mensaje,
                onReintentar = viewModel::refrescar,
                modifier = modifier
            )
            is UiState.Exito -> Column(modifier = modifier.fillMaxSize()) {
                aviso?.let { texto ->
                    AvisoSinConexion(texto = texto, onReintentar = viewModel::refrescar)
                }

                // Campo de búsqueda futurista con neón
                OutlinedTextField(
                    value = queryBusqueda,
                    onValueChange = viewModel::onBusquedaChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar smartphones neón...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.secondary) },
                    trailingIcon = {
                        if (queryBusqueda.isNotBlank()) {
                            IconButton(onClick = { viewModel.onBusquedaChanged("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )

                if (e.datos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val mensajeVacio = if (queryBusqueda.isNotBlank()) {
                            "No se encontraron smartphones con ese término"
                        } else {
                            "No hay productos disponibles"
                        }
                        Text(
                            text = mensajeVacio,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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

@Composable
fun AvisoSinConexion(texto: String, onReintentar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = texto,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onReintentar) { Text("Reintentar") }
    }
}

@Composable
fun ItemFila(item: Item, onClick: () -> Unit, onFavorito: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imagenUrl,
                contentDescription = item.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Badge de marca vibrante
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = item.subtitulo,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            BotonFavorito(esFavorito = item.esFavorito, onClick = onFavorito)
        }
    }
}
