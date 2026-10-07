package com.saludplus.citas.ui.screens.agendamiento

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.components.BotonPrincipal
import com.saludplus.citas.ui.components.TopBarSaludPlus

@Composable
fun FechaHoraScreen(navController: NavHostController, medicoId: String) {
    var fecha by remember { mutableStateOf("15/10/2026") }
    var hora by remember { mutableStateOf("10:00 AM") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopBarSaludPlus(
                titulo = "Seleccionar Fecha y Hora",
                onAtrasClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (DD/MM/AAAA)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = hora,
                onValueChange = { hora = it },
                label = { Text("Hora (ej. 10:00 AM)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            BotonPrincipal(
                texto = "Continuar a Confirmación",
                onClick = {
                    if (fecha.isBlank() || hora.isBlank()) {
                        Toast.makeText(context, "Ingrese fecha y hora", Toast.LENGTH_SHORT).show()
                    } else {
                        navController.navigate(Rutas.ConfirmarCita.crearRuta(medicoId, fecha, hora))
                    }
                }
            )
        }
    }
}