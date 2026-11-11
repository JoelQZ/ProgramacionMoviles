# Documento SDD: Aplicación "Mis Cursos" con Autenticación y CRUD en la Nube (MVVM)

Este documento contiene la especificación de arquitectura y el código completo bajo el patrón **Model-View-ViewModel (MVVM)** para el laboratorio Android utilizando **Jetpack Compose**, **Firebase Authentication** y **Cloud Firestore**.

---

## 1. Contexto y Especificación de Diseño (UI/UX)

* **Nombre del Proyecto:** `AuthFirebaseAppJoel`
* **Package Name:** `com.tecsup.authfirebaseappjoel`
* **Estilo Visual:** Colores corporativos vivos de Tecsup (Azul Celeste `#00A8E8`, Azul Océano `#0077B6`, Azul Marino Profundo `#1A237E` y acentos lavanda/glaciar en degradado).
* **Firma institucional:** Encabezado con `"Programación Móvil - Tecsup"`, logo distintivo en azul celeste y pie de página `"Joel - Tecsup"`.
* **Seguridad de Datos:** Las consultas a Cloud Firestore están estrictamente filtradas por el ID del usuario autenticado (`userId`).

---

## 2. Arquitectura del Proyecto (Patrón MVVM)

```
com.tecsup.authfirebaseappjoel/
│
├── model/
│   └── Course.kt                # Data Class que representa la entidad Curso
│
├── repository/
│   └── CourseRepository.kt      # Operaciones CRUD con Firebase Firestore
│
├── viewmodel/
│   ├── AuthViewModel.kt         # Gestión de estado de Autenticación
│   └── CourseViewModel.kt       # Gestión del estado del CRUD de Cursos
│
├── ui/
│   ├── AuthScreen.kt            # Interfaz de Login y Registro de Usuarios
│   └── HomeScreen.kt            # Interfaz del CRUD "Mis Cursos"
│
├── navigation/
│   └── AppNavigation.kt        # Control del flujo de pantallas
│
└── MainActivity.kt              # Punto de entrada principal
```

---

## 3. Código de Implementación

### 3.1. Modelo de Datos (`model/Course.kt`)

```kotlin
package com.tecsup.authfirebaseappjoel.model

data class Course(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val credits: Int = 0,
    val userId: String = ""
)
```

---

### 3.2. Repositorio (`repository/CourseRepository.kt`)

```kotlin
package com.tecsup.authfirebaseappjoel.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tecsup.authfirebaseappjoel.model.Course
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CourseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val coursesCollection = db.collection("courses")

    // Escuchar cursos en tiempo real filtrados por el usuario autenticado
    fun getCoursesByUser(userId: String): Flow<List<Course>> = callbackFlow {
        val subscription = coursesCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val courses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Course::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(courses)
            }
        awaitClose { subscription.remove() }
    }

    // Agregar curso
    async fun addCourse(course: Course) {
        coursesCollection.add(course).await()
    }

    // Actualizar curso
    async fun updateCourse(course: Course) {
        coursesCollection.document(course.id).set(course).await()
    }

    // Eliminar curso
    async fun deleteCourse(courseId: String) {
        coursesCollection.document(courseId).delete().await()
    }
}
```

---

### 3.3. ViewModels

#### `viewmodel/AuthViewModel.kt`

```kotlin
package com.tecsup.authfirebaseappjoel.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var isRegistering by mutableStateOf(false)
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    val currentUser get() = auth.currentUser

    fun login(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Por favor completa todos los campos"
            return
        }
        isLoading = true
        errorMessage = null
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) onSuccess()
                else errorMessage = task.exception?.localizedMessage ?: "Error al iniciar sesión"
            }
    }

    fun register(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "Por favor completa todos los campos"
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }
        isLoading = true
        errorMessage = null
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) onSuccess()
                else errorMessage = task.exception?.localizedMessage ?: "Error al registrar"
            }
    }

    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        onSuccess()
    }
}
```

#### `viewmodel/CourseViewModel.kt`

```kotlin
package com.tecsup.authfirebaseappjoel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.tecsup.authfirebaseappjoel.model.Course
import com.tecsup.authfirebaseappjoel.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseViewModel(
    private val repository: CourseRepository = CourseRepository()
) : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCourses()
    }

    fun loadCourses() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getCoursesByUser(uid).collect { list ->
                    _courses.value = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                _isLoading.value = false
            }
        }
    }

    fun saveCourse(id: String?, name: String, code: String, creditsStr: String, onSuccess: () -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (name.isBlank() || code.isBlank() || creditsStr.isBlank()) {
            _error.value = "Todos los campos son obligatorios"
            return
        }

        val credits = creditsStr.toIntOrNull()
        if (credits == null || credits <= 0) {
            _error.value = "Los créditos deben ser un número positivo"
            return
        }

        viewModelScope.launch {
            try {
                if (id == null) {
                    val newCourse = Course(name = name, code = code, credits = credits, userId = uid)
                    repository.addCourse(newCourse)
                } else {
                    val updatedCourse = Course(id = id, name = name, code = code, credits = credits, userId = uid)
                    repository.updateCourse(updatedCourse)
                }
                _error.value = null
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCourse(courseId)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
```

---

### 3.4. Vistas UI (Jetpack Compose con Paleta Tecsup)

#### `ui/AuthScreen.kt`

```kotlin
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
```

#### `ui/HomeScreen.kt`

```kotlin
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

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF0F8FF),
            Color(0xFFE0F7FA)
        )
    )
    val tecsupBlue = Color(0xFF0288D1)
    val tecsupDarkBlue = Color(0xFF0077B6)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Encabezado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mis Cursos 📚",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )
                    Text(
                        text = authViewModel.currentUser?.email ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFF455A64)
                    )
                }

                Button(
                    onClick = { authViewModel.logout(onLogout) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Salir", fontSize = 12.sp)
                }
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

            // Lista de cursos
            if (isLoading && courses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = tecsupBlue)
                }
            } else if (courses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No tienes cursos registrados. Registra uno con el botón '+'.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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

            // Footer
            Text(
                text = "Joel - Tecsup",
                fontSize = 12.sp,
                color = Color(0xFF78909C),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // Botón flotante para agregar
        FloatingActionButton(
            onClick = {
                selectedCourse = null
                showDialog = true
            },
            containerColor = tecsupDarkBlue,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 36.dp, end = 8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar Curso")
        }

        // Dialogo Formulario (Crear / Editar)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                Text(text = "Código: ${course.code}", fontSize = 14.sp, color = Color.Gray)
                Text(text = "Créditos: ${course.credits}", fontSize = 14.sp, color = Color(0xFF0288D1))
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF0288D1))
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
        title = { Text(if (course == null) "Agregar Curso ✨" else "Editar Curso ✏️") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del curso") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Código (Ej. MA-101)") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = credits,
                    onValueChange = { credits = it },
                    label = { Text("Créditos") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, code, credits) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6))
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
```

---

### 3.5. Navegación (`navigation/AppNavigation.kt`)

```kotlin
package com.tecsup.authfirebaseappjoel.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.tecsup.authfirebaseappjoel.ui.AuthScreen
import com.tecsup.authfirebaseappjoel.ui.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
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

---

### 3.6. Entrada Principal (`MainActivity.kt`)

```kotlin
package com.tecsup.authfirebaseappjoel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.tecsup.authfirebaseappjoel.navigation.AppNavigation

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