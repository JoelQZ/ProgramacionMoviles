package com.tecsup.lavacampus.ui.graficos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.ui.theme.ColorAdministrador
import java.util.Locale

@Composable
fun GraficoBarras(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier
) {
    val maxValor = (datos.maxOfOrNull { it.valor } ?: 1f).coerceAtLeast(1f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        datos.forEach { dato ->
            val porcentaje = (dato.valor / maxValor).coerceIn(0.12f, 1f)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = String.format(Locale.US, "%.0f", dato.valor),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .width(38.dp)
                        .fillMaxHeight(porcentaje * 0.7f)
                        .background(ColorAdministrador, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = dato.etiqueta,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}