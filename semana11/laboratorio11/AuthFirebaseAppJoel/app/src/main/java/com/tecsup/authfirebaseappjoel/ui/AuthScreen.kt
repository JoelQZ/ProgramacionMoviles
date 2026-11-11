package com.tecsup.authfirebaseappjoel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.authfirebaseappjoel.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF0F8FF),
            Color(0xFFE0F7FA)
        )
    )
    val tecsupBlue = Color(0xFF0288D1)
    val tecsupDarkBlue = Color(0xFF0077B6)
    val tecsupCyan = Color(0xFF00A8E8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(20.dp)
    ) {
        // Encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Programación Móvil - Tecsup",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF455A64)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Tecsup",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tecsupBlue,
                letterSpacing = 1.sp
            )
        }

        // Formulario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (viewModel.isRegistering) "Crear cuenta ✨" else "Iniciar Sesión 🔐",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tecsupCyan,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.85f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tecsupCyan,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.85f)
                )
            )

            if (viewModel.isRegistering) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = { viewModel.confirmPassword = it },
                    label = { Text("Confirmar contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tecsupCyan,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.85f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            viewModel.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (viewModel.isLoading) {
                CircularProgressIndicator(color = tecsupBlue)
            } else {
                Button(
                    onClick = {
                        if (viewModel.isRegistering) viewModel.register(onLoginSuccess)
                        else viewModel.login(onLoginSuccess)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = tecsupDarkBlue)
                ) {
                    Text(
                        text = if (viewModel.isRegistering) "Registrarse" else "Ingresar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(onClick = {
                    viewModel.isRegistering = !viewModel.isRegistering
                    viewModel.errorMessage = null
                }) {
                    Text(
                        text = if (viewModel.isRegistering) "— Volver al login —" else "¿No tienes cuenta? Regístrate aquí",
                        color = tecsupDarkBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Pie de página
        Text(
            text = "Joel - Tecsup",
            fontSize = 12.sp,
            color = Color(0xFF78909C),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}
