package com.saludplus.citas.ui.screens.resultados

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.saludplus.citas.ui.components.TopBarSaludPlus

@Composable
fun ResultadosScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopBarSaludPlus(
                titulo = "Resultados Médicos",
                onAtrasClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No hay resultados médicos disponibles actualmente.")
        }
    }
}