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
