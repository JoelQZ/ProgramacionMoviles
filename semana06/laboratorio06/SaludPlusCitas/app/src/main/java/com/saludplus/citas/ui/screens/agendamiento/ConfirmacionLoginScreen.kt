package com.saludplus.citas.ui.screens.agendamiento

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.saludplus.citas.navigation.Rutas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmacionLoginScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var codigoPin by remember { mutableStateOf("") }
    val codigoCorrecto = "1234" // Código de prueba

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verificación de seguridad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Código de Confirmación",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hemos enviado un código de 4 dígitos para confirmar su inicio de sesión. (Código: 1234)",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val char = codigoPin.getOrNull(i)?.toString() ?: ""
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .border(
                                width = 2.dp,
                                color = if (char.isNotEmpty()) Color(0xFF2563EB) else Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = codigoPin,
                onValueChange = { input ->
                    if (input.length <= 4 && input.all { it.isDigit() }) {
                        codigoPin = input
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Ingresa el código (4 dígitos)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (codigoPin == codigoCorrecto) {
                        Toast.makeText(context, "Verificación exitosa", Toast.LENGTH_SHORT).show()
                        navController.navigate(Rutas.Home.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Código incorrecto. Ingresa 1234", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = codigoPin.length == 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Verificar y Continuar", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}