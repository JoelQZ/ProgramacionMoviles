package com.tecsup.authfirebaseappjoel.repository

import com.google.firebase.firestore.FirebaseFirestore
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
    suspend fun addCourse(course: Course) {
        coursesCollection.add(course).await()
    }

    // Actualizar curso
    suspend fun updateCourse(course: Course) {
        coursesCollection.document(course.id).set(course).await()
    }

    // Eliminar curso
    suspend fun deleteCourse(courseId: String) {
        coursesCollection.document(courseId).delete().await()
    }
}
