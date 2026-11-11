package com.tecsup.catalogo.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.catalogo.domain.model.Item
import com.tecsup.catalogo.ui.UiState
import com.tecsup.catalogo.ui.components.BotonFavorito
import com.tecsup.catalogo.ui.components.Cargando
import com.tecsup.catalogo.ui.components.ErrorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(viewModel: DetalleViewModel, onVolver: () -> Unit) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚡ DETALLE CYBER",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
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
        val modifier = Modifier.padding(padding).fillMaxSize()
        when (val e = estado) {
            is UiState.Cargando -> Cargando(modifier)
            is UiState.Error -> ErrorView(
                mensaje = e.mensaje,
                onReintentar = viewModel::cargar,
                modifier = modifier
            )
            is UiState.Exito -> DetalleContenido(
                item = e.datos,
                onFavorito = viewModel::alternarFavorito,
                modifier = modifier
            )
        }
    }
}

@Composable
fun DetalleContenido(item: Item, onFavorito: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Contenedor de imagen con fondo de superficie y esquinas redondeadas
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = item.imagenUrl,
                    contentDescription = item.titulo,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            BotonFavorito(esFavorito = item.esFavorito, onClick = onFavorito)
        }

        // Badge de marca/subtítulo
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = item.subtitulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        // Módulos futuristas de Rating y Stock
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DatoFuturista("RATING", item.rating.toString() + " ★", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            DatoFuturista("STOCK DISPONIBLE", item.stock.toString() + " u.", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
        }

        Text(
            text = "ESPECIFICACIONES:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = item.descripcion.ifBlank { "Sin descripción técnica detallada para este dispositivo." },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DatoFuturista(titulo: String, valor: String, containerColor: Color, contentColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
