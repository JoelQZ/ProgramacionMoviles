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
