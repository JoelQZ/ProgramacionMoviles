package com.tecsup.authfirebaseappjoel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.authfirebaseappjoel.model.Course
import com.tecsup.authfirebaseappjoel.viewmodel.AuthViewModel
import com.tecsup.authfirebaseappjoel.viewmodel.CourseViewModel

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    courseViewModel: CourseViewModel = viewModel()
) {
    val courses by courseViewModel.courses.collectAsState()
    val isLoading by courseViewModel.isLoading.collectAsState()
    val errorMessage by courseViewModel.error.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }

    // Colores corporativos Tecsup
    val tecsupVibrantCyan = Color(0xFF00A8E8) // Azul celeste vibrante
    val tecsupOceanBlue = Color(0xFF0077B6)   // Azul océano
    val tecsupNavy = Color(0xFF1A237E)        // Azul marino profundo

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF), // Blanco puro
            Color(0xFFF0F8FF), // Celeste glacial
            Color(0xFFE0F7FA)  // Celeste claro
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- HEADER TECSUP ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Programación Móvil - Tecsup",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF455A64)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tecsup",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tecsupVibrantCyan,
                        letterSpacing = 1.sp
                    )

                    Button(
                        onClick = { authViewModel.logout(onLogout) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar Sesión", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFB0BEC5).copy(alpha = 0.5f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Subtítulo de Cursos y usuario activo
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Mis Cursos 📚",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = tecsupNavy
                )
                Text(
                    text = "Usuario: ${authViewModel.currentUser?.email ?: "usuario@tecsup.edu.pe"}",
                    fontSize = 13.sp,
                    color = Color(0xFF546E7A),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Lista de Cursos CRUD
            if (isLoading && courses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = tecsupVibrantCyan)
                }
            } else if (courses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No tienes cursos registrados. Toca '+' para agregar uno.",
                        color = Color(0xFF78909C),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(courses) { course ->
                        CourseCard(
                            course = course,
                            onEdit = {
                                selectedCourse = course
                                showDialog = true
                            },
                            onDelete = { courseViewModel.deleteCourse(course.id) }
                        )
                    }
                }
            }

            // --- FOOTER TECSUP ---
            Text(
                text = "Joel - Tecsup",
                fontSize = 12.sp,
                color = Color(0xFF78909C),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // Botón Flotante (FAB) para Agregar Cursos
        FloatingActionButton(
            onClick = {
                selectedCourse = null
                showDialog = true
            },
            containerColor = tecsupOceanBlue,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 36.dp, end = 8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar Curso")
        }

        // Modal Diálogo Formulario (Crear / Editar)
        if (showDialog) {
            CourseFormDialog(
                course = selectedCourse,
                onDismiss = {
                    showDialog = false
                    courseViewModel.clearError()
                },
                onSave = { name, code, credits ->
                    courseViewModel.saveCourse(
                        id = selectedCourse?.id,
                        name = name,
                        code = code,
                        creditsStr = credits
                    ) {
                        showDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun CourseCard(course: Course, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0077B6)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Código: ${course.code}",
                        fontSize = 14.sp,
                        color = Color(0xFF546E7A)
                    )
                    Text(
                        text = "Créditos: ${course.credits}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00A8E8)
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF00A8E8))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun CourseFormDialog(
    course: Course?,
    onDismiss: () -> Unit,
    onSave: (name: String, code: String, credits: String) -> Unit
) {
    var name by remember { mutableStateOf(course?.name ?: "") }
    var code by remember { mutableStateOf(course?.code ?: "") }
    var credits by remember { mutableStateOf(course?.credits?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (course == null) "Agregar Curso ✨" else "Editar Curso ✏️", color = Color(0xFF1A237E)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del curso") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00A8E8)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Código (Ej. CUS-101)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00A8E8)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = credits,
                    onValueChange = { credits = it },
                    label = { Text("Créditos") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00A8E8)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, code, credits) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}
