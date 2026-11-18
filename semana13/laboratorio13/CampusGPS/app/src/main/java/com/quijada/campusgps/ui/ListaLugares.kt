package com.quijada.campusgps.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    ultimoLugarGuardadoId: Int? = null,
    modoOscuro: Boolean = false,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(
            items = lugares,
            key = { it.lugar.id }
        ) { item ->
            val esSeleccionado = item.lugar.id == seleccionado
            val esMasCercano = item.lugar.id == masCercanoId
            val esUltimoGuardado = item.lugar.id == ultimoLugarGuardadoId

            val colorIndicador = if (modoOscuro) {
                if (esMasCercano) Color(0xFF10B981) else (if (esSeleccionado) Color(0xFF0F172A) else Color(0xFF94A3B8))
            } else {
                if (esMasCercano) {
                    if (esSeleccionado) VerdeOscuro else VerdeCercano
                } else {
                    if (esSeleccionado) AzulOscuro else Color.White.copy(alpha = 0.8f)
                }
            }

            val cardContainerColor = if (modoOscuro) {
                if (esSeleccionado) Color(0xFF38BDF8) else Color(0xFF1E293B)
            } else {
                if (esSeleccionado) Color.White else Color.White.copy(alpha = 0.14f)
            }

            val cardBorder = if (modoOscuro) {
                if (esSeleccionado) BorderStroke(2.dp, Color(0xFF7DD3FC)) else BorderStroke(1.dp, Color(0xFF334155))
            } else {
                if (esSeleccionado) BorderStroke(2.dp, AzulClaro) else null
            }

            val titleColor = if (modoOscuro) {
                if (esSeleccionado) Color(0xFF0F172A) else Color(0xFFF8FAFC)
            } else {
                if (esSeleccionado) AzulOscuro else Color.White
            }

            val categoryColor = if (modoOscuro) {
                if (esSeleccionado) Color(0xFF0F172A).copy(alpha = 0.75f) else Color(0xFF94A3B8)
            } else {
                if (esSeleccionado) Color.Gray else Color.White.copy(alpha = 0.75f)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = cardContainerColor),
                shape = RoundedCornerShape(16.dp),
                border = cardBorder,
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.lugar.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = titleColor
                            )
                            if (esUltimoGuardado && !esSeleccionado) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (modoOscuro) Color(0xFF334155) else Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Último visitado",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (modoOscuro) Color(0xFFF8FAFC) else Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = item.lugar.categoria,
                            style = MaterialTheme.typography.bodySmall,
                            color = categoryColor
                        )
                    }

                    if (item.dentroDelRadio) {
                        Surface(
                            color = if (modoOscuro) {
                                if (esSeleccionado) Color(0xFF0F172A).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.2f)
                            } else {
                                if (esSeleccionado) Color(0xFFE8F5E9) else Color(0xFF00E676).copy(alpha = 0.25f)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "En radio",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (modoOscuro) {
                                    if (esSeleccionado) Color(0xFF065F46) else Color(0xFF34D399)
                                } else {
                                    if (esSeleccionado) Color(0xFF1B5E20) else Color(0xFF00E676)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    val textoDistancia = item.metros?.let { formatearDistancia(it) } ?: "Sin calcular"

                    if (esSeleccionado) {
                        Surface(
                            color = if (modoOscuro) Color(0xFF0F172A) else AzulClaro,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = textoDistancia,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (modoOscuro) Color(0xFF38BDF8) else AzulOscuro,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Text(
                            text = textoDistancia,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (esMasCercano) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (modoOscuro) {
                                if (esMasCercano) Color(0xFF34D399) else Color(0xFF38BDF8)
                            } else {
                                if (esMasCercano) VerdeCercano else Color.White.copy(alpha = 0.9f)
                            }
                        )
                    }
                }
            }
        }
    }
}
