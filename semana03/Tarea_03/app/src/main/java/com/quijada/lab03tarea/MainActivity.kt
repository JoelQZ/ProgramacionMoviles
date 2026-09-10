package com.quijada.lab03tarea
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaRegistroNotas()
                }
            }
        }
    }
}

@Composable
fun PantallaRegistroNotas() {
    var notaFundamentos by remember { mutableFloatStateOf(0f) }
    var notaPOO by remember { mutableFloatStateOf(0f) }
    var notaMoviles by remember { mutableFloatStateOf(0f) }
    var notaBD by remember { mutableFloatStateOf(0f) }

    var redondear by remember { mutableStateOf(false) }
    var confirmado by remember { mutableStateOf(false) }

    var mostrarResultado by remember { mutableStateOf(false) }
    var promPonderado by remember { mutableDoubleStateOf(0.0) }
    var promFinalTexto by remember { mutableStateOf("") }
    var observacionTexto by remember { mutableStateOf("") }
    var colorChip by remember { mutableStateOf(Color.Gray) }

    val fondoGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFEDE7F6), Color(0xFFF3E5F5))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoGradient)
            .verticalScroll(rememberScrollState())
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF5E49A6))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Registro de Notas",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Notas del ciclo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Desliza para asignar cada nota (0 a 20)",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilaCurso("Fundamentos de Programación", 20, notaFundamentos) { notaFundamentos = it }
            FilaCurso("Programación Orientada a Objetos", 25, notaPOO) { notaPOO = it }
            FilaCurso("Programación en Móviles", 30, notaMoviles) { notaMoviles = it }
            FilaCurso("Base de Datos", 25, notaBD) { notaBD = it }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Redondear promedio final", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = redondear,
                    onCheckedChange = { redondear = it }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = confirmado,
                    onCheckedChange = { confirmado = it }
                )
                Text("Confirmo que las notas son correctas", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val ponderado = (notaFundamentos.toInt() * 0.20) +
                            (notaPOO.toInt() * 0.25) +
                            (notaMoviles.toInt() * 0.30) +
                            (notaBD.toInt() * 0.25)

                    promPonderado = ponderado

                    val finalVal = if (redondear) {
                        ponderado.roundToInt().toDouble()
                    } else {
                        ponderado
                    }

                    promFinalTexto = if (redondear) {
                        "${finalVal.toInt()}"
                    } else {
                        String.format(Locale.US, "%.2f", finalVal)
                    }

                    when {
                        finalVal >= 17.0 -> {
                            observacionTexto = "EXCELENTE"
                            colorChip = Color(0xFF1B5E20)
                        }
                        finalVal >= 13.0 -> {
                            observacionTexto = "APROBADO"
                            colorChip = Color(0xFF2E7D32)
                        }
                        finalVal >= 10.0 -> {
                            observacionTexto = "EN RECUPERACIÓN"
                            colorChip = Color(0xFFF57C00)
                        }
                        else -> {
                            observacionTexto = "DESAPROBADO"
                            colorChip = Color(0xFFD32F2F)
                        }
                    }

                    mostrarResultado = true
                },
                enabled = confirmado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E49A6),
                    disabledContainerColor = Color(0xFFC5CAE9)
                )
            ) {
                Text("CALCULAR PROMEDIO", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!mostrarResultado) {
                Text(
                    text = "Asigna las notas y confirma para calcular",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Promedio ponderado:  ${String.format(Locale.US, "%.2f", promPonderado)}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "Promedio final:  $promFinalTexto",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5E49A6)
                            )
                            if (redondear) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(redondeado)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = colorChip.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = observacionTexto,
                                color = colorChip,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "✓ Promedio calculado correctamente",
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Desarrollado por: Joel Quijada",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun FilaCurso(
    nombreCurso: String,
    peso: Int,
    nota: Float,
    onNotaChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(nombreCurso, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("($peso%)", color = Color(0xFF7E57C2), fontSize = 13.sp)
            }

            Surface(
                color = Color(0xFFEDE7F6),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "${nota.toInt()}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5E49A6),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Slider(
            value = nota,
            onValueChange = onNotaChange,
            valueRange = 0f..20f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF5E49A6),
                activeTrackColor = Color(0xFF5E49A6)
            )
        )
    }
}