package com.quijada.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.quijada.centrotareas.model.PrimosUi
import com.quijada.centrotareas.viewmodel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(
    viewModel: TareasViewModel,
    modifier: Modifier = Modifier
) {
    val mensaje by viewModel.mensaje.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val corriendo by viewModel.corriendo.collectAsState()
    val primos by viewModel.primos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CentroTareas",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaTareaConProgreso(
                mensaje = mensaje,
                progreso = progreso,
                corriendo = corriendo,
                onTareaLarga = { viewModel.tareaLarga() },
                onCancelarTarea = { viewModel.cancelarTarea() }
            )
            TarjetaContarPrimos(
                primosState = primos,
                onCalcularPrimos = { hasta -> viewModel.calcularPrimos(hasta) },
                onCancelarPrimos = { viewModel.cancelarPrimos() }
            )
            TarjetaDosCargas()
        }
    }
}

@Composable
fun TarjetaTareaConProgreso(
    mensaje: String,
    progreso: Float,
    corriendo: Boolean,
    onTareaLarga: () -> Unit,
    onCancelarTarea: () -> Unit
) {
    var contador by remember { mutableIntStateOf(0) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tarea con progreso",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { contador++ }) {
                    Text(text = "Tócame: $contador")
                }
                Button(onClick = onTareaLarga) {
                    Text(text = "Iniciar")
                }
                OutlinedButton(
                    onClick = onCancelarTarea,
                    enabled = corriendo
                ) {
                    Text(text = "Cancelar")
                }
            }

            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${(progreso * 100).toInt()} %",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun TarjetaContarPrimos(
    primosState: PrimosUi,
    onCalcularPrimos: (Int) -> Unit,
    onCancelarPrimos: () -> Unit
) {
    var textoNumero by remember { mutableStateOf("500000") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Contar números primos",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = textoNumero,
                onValueChange = { textoNumero = it },
                label = { Text("Hasta el número") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val hasta = textoNumero.toIntOrNull() ?: 0
                        onCalcularPrimos(hasta)
                    },
                    enabled = primosState !is PrimosUi.Calculando
                ) {
                    Text(text = "Contar")
                }

                OutlinedButton(
                    onClick = onCancelarPrimos,
                    enabled = primosState is PrimosUi.Calculando
                ) {
                    Text(text = "Cancelar")
                }
            }

            when (primosState) {
                is PrimosUi.Inactivo -> {}
                is PrimosUi.Calculando -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Text(text = "Calculando primos...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                is PrimosUi.Listo -> {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Primos hasta ${primosState.hasta}: ${primosState.cantidad}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "Tiempo: ${primosState.duracionMs} ms",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
                is PrimosUi.Cancelado -> {
                    Text(
                        text = "Cancelado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaDosCargas() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Dos cargas",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Estructura inicial",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
