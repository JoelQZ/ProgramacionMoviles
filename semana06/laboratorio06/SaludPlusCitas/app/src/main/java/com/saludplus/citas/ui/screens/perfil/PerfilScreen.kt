package com.saludplus.citas.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.TopBarSaludPlus

@Composable
fun PerfilScreen(navController: NavHostController) {
    val usuario = Repositorio.usuarioActual

    Scaffold(
        topBar = {
            TopBarSaludPlus(
                titulo = "Mi Perfil",
                onAtrasClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Perfil",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = usuario?.nombre ?: "Joel Quijada",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Correo electrónico:", fontWeight = FontWeight.SemiBold)
                    Text(text = usuario?.email ?: "joel.quijada@tecsup.edu.pe")
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Teléfono:", fontWeight = FontWeight.SemiBold)
                    Text(text = usuario?.telefono ?: "987654321")
                }
            }
        }
    }
}