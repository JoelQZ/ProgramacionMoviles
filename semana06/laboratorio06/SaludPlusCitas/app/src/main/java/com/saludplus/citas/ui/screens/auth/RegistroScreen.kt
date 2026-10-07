package com.saludplus.citas.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saludplus.citas.data.model.Usuario
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.theme.AzulPrincipal

@Composable
fun RegistroScreen(navController: NavHostController) {
    var nombre by remember { mutableStateOf("Joel Quijada") }
    var telefono by remember { mutableStateOf("987 654 321") }
    var email by remember { mutableStateOf("joel.quijada@tecsup.edu.pe") }
    var clave by remember { mutableStateOf("123456") }
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Crear cuenta", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Regístrate para agendar tus citas", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo (opcional)") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isBlank() || clave.isBlank()) {
                        Toast.makeText(context, "Ingresa un correo y contraseña", Toast.LENGTH_SHORT).show()
                    } else {
                        val nuevoUsuario = Usuario(
                            nombre = nombre,
                            telefono = telefono,
                            email = email,
                            clave = clave
                        )
                        Repositorio.registrarUsuario(nuevoUsuario)
                        Toast.makeText(context, "Cuenta creada. ¡Inicia sesión!", Toast.LENGTH_SHORT).show()

                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(Rutas.Registro.ruta) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
            ) {
                Text(text = "Registrarme", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Al registrarte aceptas nuestros\nTérminos y Condiciones",
                fontSize = 12.sp,
                color = AzulPrincipal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "¿Ya tienes cuenta? ", fontSize = 14.sp)
                TextButton(onClick = { navController.navigate(Rutas.Login.ruta) }) {
                    Text(text = "Iniciar sesión", color = AzulPrincipal, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}