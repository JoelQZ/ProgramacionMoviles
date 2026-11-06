package com.tecsup.authfirebaseappjoel

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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()

    var isRegistering by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Paleta de colores oficial estilo Tecsup (Celeste / Azul / Blanco)
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF), // Blanco puro superior
            Color(0xFFF0F8FF), // Azul Alicia / Celeste glacial
            Color(0xFFE0F7FA)  // Celeste claro inferior
        )
    )
    val tecsupBlue = Color(0xFF0288D1)      // Azul Celeste Tecsup
    val tecsupDarkBlue = Color(0xFF0077B6)  // Azul Océano para botones principales
    val tecsupCyan = Color(0xFF00A8E8)      // Celeste brillante para enfocar campos

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(20.dp)
    ) {
        // --- HEADER ---
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

            Spacer(modifier = Modifier.height(24.dp))

            // Logo estilizado Tecsup
            Text(
                text = "Tecsup",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tecsupBlue,
                letterSpacing = 1.sp
            )
        }

        // --- FORMULARIO CENTRAL ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRegistering) "Crear cuenta ✨" else "Iniciar Sesión 🔐",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tecsupCyan,
                    unfocusedBorderColor = Color(0xFFB0BEC5),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.85f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tecsupCyan,
                    unfocusedBorderColor = Color(0xFFB0BEC5),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.85f)
                )
            )

            // Campo extra si se selecciona 'Crear cuenta'
            if (isRegistering) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tecsupCyan,
                        unfocusedBorderColor = Color(0xFFB0BEC5),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.85f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(color = tecsupBlue)
            } else {
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Por favor completa todos los campos"
                            return@Button
                        }

                        if (isRegistering && password != confirmPassword) {
                            errorMessage = "Las contraseñas no coinciden"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        if (isRegistering) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Error al registrar usuario"
                                    }
                                }
                        } else {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Credenciales incorrectas"
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = tecsupDarkBlue)
                ) {
                    Text(
                        text = if (isRegistering) "Registrarse" else "Ingresar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(
                    onClick = {
                        isRegistering = !isRegistering
                        errorMessage = null
                    }
                ) {
                    Text(
                        text = if (isRegistering) "— Volver al login —" else "¿No tienes cuenta? Regístrate aquí",
                        color = tecsupDarkBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // --- FOOTER ---
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
