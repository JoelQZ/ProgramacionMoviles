# Documento SDD: Autenticación de Usuarios con Firebase y Jetpack Compose

Este documento detalla el desarrollo guiado por especificaciones (**Specification-Driven Development - SDD**) para el laboratorio de autenticación en Android utilizando **Jetpack Compose** y **Firebase Authentication**.

## 1. Especificación del Software (Specification Context)

* **Nombre de la App:** `AuthFirebaseAppJoel`

* **Package Name:** `com.tecsup.authfirebaseappjoel`

* **Estilo Visual (Paleta Tecsup):** Colors corporativos de Tecsup basados en Azul Celeste Tecsup (`#0288D1`), Azul Oceánico (`#0077B6`), fondo en degradado blanco/celeste ártico (`#F0F8FF` / `#E0F7FA`), bordes redondeados y tipografía limpia.

* **Componentes de UI incluidos:**

  1. Header: `"Programación Móvil - Tecsup"`

  2. Logo distintivo con texto estilizado **Tecsup** en Azul Celeste oficial.

  3. Pantalla de Login con icono de candado ($\text{🔐}$) y acceso a Registro.

  4. Pantalla de Registro con icono de destellos ($\text{✨}$) y confirmación de contraseña.

  5. Pantalla de Bienvenida con icono de fiesta ($\text{🎉}$) y muestra del correo del usuario activo.

  6. Footer: `"Joel - Tecsup"` (Nombre del estudiante y la institución).

## 2. Arquitectura de Componentes

El proyecto se compone de 4 estructuras principales:

| Archivo | Responsabilidad SDD | 
| ----- | ----- | 
| `AuthScreen.kt` | Gestiona los estados de Login y Registro de Usuario con colores corporativos Tecsup (Celeste/Azul/Blanco) y validación completa. | 
| `HomeScreen.kt` | Muestra el estado del usuario autenticado y maneja el cierre de sesión con estilo Tecsup. | 
| `AppNavigation.kt` | Controla la pila de navegación (*Backstack*) entre `AuthScreen` y `HomeScreen`. | 
| `MainActivity.kt` | Punto de entrada que inicializa la superficie UI con el tema visual. | 

## 3. Código de Implementación (Jetpack Compose)

### 3.1. `AuthScreen.kt`

```kotlin
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
```

### 3.2. `HomeScreen.kt`

```kotlin
package com.tecsup.authfirebaseappjoel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF0F8FF),
            Color(0xFFE0F7FA)
        )
    )
    val tecsupDarkBlue = Color(0xFF0077B6)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido/a 🎉",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Sesión iniciada como: ${user?.email ?: "usuario@tecsup.edu.pe"}",
                fontSize = 15.sp,
                color = Color(0xFF37474F),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = tecsupDarkBlue)
            ) {
                Text(
                    text = "Cerrar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

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
```

### 3.3. `AppNavigation.kt`

```kotlin
package com.tecsup.authfirebaseappjoel

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    // Si ya existe sesión iniciada, entra directo a 'home'
    val startDestination = if (auth.currentUser != null) "home" else "auth"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
```

### 3.4. `MainActivity.kt`

```kotlin
package com.tecsup.authfirebaseappjoel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        Surface {
          AppNavigation()
        }
      }
    }
  }
}
```