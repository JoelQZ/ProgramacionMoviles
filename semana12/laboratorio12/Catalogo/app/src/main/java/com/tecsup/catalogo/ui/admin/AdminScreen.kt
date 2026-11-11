package com.tecsup.catalogo.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.catalogo.ui.UiState
import com.tecsup.catalogo.ui.components.Cargando
import com.tecsup.catalogo.ui.components.ErrorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel, onVolver: () -> Unit) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val sincronizando by viewModel.sincronizando.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚙ PANEL ADMIN CYBER",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
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
            is UiState.Error -> ErrorView(mensaje = e.mensaje, onReintentar = viewModel::sincronizar, modifier = modifier)
            is UiState.Exito -> Column(
                modifier = modifier
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Tarjetas métricas futuristas
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TotalFuturista("GUARDADOS", e.datos.guardados, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                    TotalFuturista("FAVORITOS", e.datos.favoritos, MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = viewModel::sincronizar,
                    enabled = !sincronizando,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = if (sincronizando) "SINCRONIZANDO SISTEMA..." else "SINCRONIZAR DATOS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = viewModel::limpiarCache,
                    enabled = !sincronizando,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "LIMPIAR CACHÉ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                mensaje?.let {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TotalFuturista(titulo: String, valor: Int, containerColor: Color, contentColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
