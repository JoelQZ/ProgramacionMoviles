package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.saludplus.citas.ui.components.TopBarSaludPlus

@Composable
fun TerminosScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopBarSaludPlus(
                titulo = "Términos y Condiciones",
                onAtrasClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Términos de Servicio de SaludPlus",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Bienvenido a SaludPlus. Al utilizar esta aplicación para la reserva y gestión de citas médicas, usted acepta cumplir con los siguientes términos y condiciones:\n\n" +
                        "1. Sus datos personales serán tratados de acuerdo con la legislación vigente de protección de datos.\n" +
                        "2. Las citas agendadas son sujetas a disponibilidad y confirmación por el establecimiento de salud.\n" +
                        "3. SaludPlus se reserva el derecho de modificar o actualizar estos términos en cualquier momento.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}