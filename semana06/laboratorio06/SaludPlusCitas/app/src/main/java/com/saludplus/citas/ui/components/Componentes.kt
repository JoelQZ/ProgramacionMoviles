package com.saludplus.citas.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSaludPlus(
    titulo: String,
    mostrarBotonAtras: Boolean = true,
    onAtrasClick: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = titulo) },
        navigationIcon = {
            if (mostrarBotonAtras) {
                IconButton(onClick = onAtrasClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        }
    )
}

@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled
    ) {
        Text(text = texto)
    }
}