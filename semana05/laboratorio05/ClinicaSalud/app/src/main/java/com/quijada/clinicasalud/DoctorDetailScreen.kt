package com.quijada.clinicasalud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoctorDetailScreen(
    medicoId: Int,
    onAgendarClick: () -> Unit
) {
    val medico = Repository.medicos.find { it.id == medicoId } ?: Repository.medicos.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(medico.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("${medico.especialidad} • ${medico.experiencia}", color = Color.Gray)

        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${medico.calificacion} (120 reseñas)",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = medico.biografia,
            color = Color.DarkGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onAgendarClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Agendar cita", fontSize = 16.sp)
        }
    }
}