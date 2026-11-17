package com.quijada.campusgps.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quijada.campusgps.model.LugarConDistancia
import com.quijada.campusgps.ui.theme.AzulClaro
import com.quijada.campusgps.ui.theme.AzulOscuro
import com.quijada.campusgps.ui.theme.VerdeCercano
import com.quijada.campusgps.ui.theme.VerdeOscuro
import com.quijada.campusgps.util.formatearDistancia

@Composable
fun ListaLugares(
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    onClick: (Int) -> Unit,
    masCercanoId: Int? = null,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = lugares,
            key = { it.lugar.id }
        ) { item ->
            val esSeleccionado = item.lugar.id == seleccionado
            val esMasCercano = item.lugar.id == masCercanoId

            val colorIndicador = if (esMasCercano) {
                if (esSeleccionado) VerdeOscuro else VerdeCercano
            } else {
                if (esSeleccionado) AzulOscuro else Color.White.copy(alpha = 0.8f)
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (esSeleccionado) Color.White else Color.White.copy(alpha = 0.14f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = if (esSeleccionado) BorderStroke(2.dp, AzulClaro) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onClick(item.lugar.id) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(colorIndicador)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = item.lugar.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (esSeleccionado) AzulOscuro else Color.White
                        )
                        Text(
                            text = item.lugar.categoria,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (esSeleccionado) Color.Gray else Color.White.copy(alpha = 0.75f)
                        )
                    }

                    val textoDistancia = item.metros?.let { formatearDistancia(it) } ?: "Sin calcular"

                    if (esSeleccionado) {
                        Surface(
                            color = AzulClaro,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = textoDistancia,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = AzulOscuro,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Text(
                            text = textoDistancia,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (esMasCercano) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (esMasCercano) VerdeCercano else Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}
