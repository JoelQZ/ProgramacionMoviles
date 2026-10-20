package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.theme.ColorEstudiante

@Composable
fun StepperEstado(
    estadoActual: EstadoPedido,
    modifier: Modifier = Modifier
) {
    val etapas = listOf(
        EstadoPedido.RECIBIDO to "Recibido",
        EstadoPedido.EN_PROCESO to "En proceso",
        EstadoPedido.LISTO to "Listo",
        EstadoPedido.ENTREGADO to "Entregado"
    )

    val ordenEstados = listOf(
        EstadoPedido.RECIBIDO,
        EstadoPedido.EN_PROCESO,
        EstadoPedido.LISTO,
        EstadoPedido.ENTREGADO
    )

    val indiceActual = ordenEstados.indexOf(estadoActual)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        etapas.forEachIndexed { index, (estado, titulo) ->
            val completado = indiceActual >= index
            val esUltimo = index == etapas.lastIndex

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (completado) ColorEstudiante else Color.White)
                            .border(
                                width = 2.dp,
                                color = if (completado) ColorEstudiante else Color.LightGray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (completado) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    if (!esUltimo) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(32.dp)
                                .background(if (indiceActual > index) ColorEstudiante else Color.LightGray)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = titulo,
                    fontSize = 14.sp,
                    fontWeight = if (completado) FontWeight.Bold else FontWeight.Normal,
                    color = if (completado) Color.Black else Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}