package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dry
import androidx.compose.material.icons.filled.Iron
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.ui.components.obtenerBrushFondoLavacampus
import com.tecsup.lavacampus.ui.theme.ColorEstudiante

@Composable
fun SplashScreen(
    onComenzar: () -> Unit
) {
    var animacionIniciada by remember { mutableStateOf(false) }
    val alfa by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "AlphaSplash"
    )

    val brushFondo = obtenerBrushFondoLavacampus()

    LaunchedEffect(Unit) {
        animacionIniciada = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brushFondo)
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-30).dp, y = 30.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = 70.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.10f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .alpha(alfa),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(26.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalLaundryService,
                            contentDescription = "Logo",
                            tint = ColorEstudiante,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "LavaCampus",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Tu ropa limpia, sin salir del campus",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaServicioSplash(Icons.Default.LocalLaundryService, "Lavado")
                    TarjetaServicioSplash(Icons.Default.Iron, "Planchado")
                    TarjetaServicioSplash(Icons.Default.Dry, "En seco")
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onComenzar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = ColorEstudiante
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text("Comenzar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ColorEstudiante)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onComenzar) {
                    Text(
                        text = "Ya tengo una cuenta",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun TarjetaServicioSplash(icono: ImageVector, etiqueta: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.size(width = 90.dp, height = 80.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = ColorEstudiante,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = etiqueta,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ColorEstudiante
            )
        }
    }
}