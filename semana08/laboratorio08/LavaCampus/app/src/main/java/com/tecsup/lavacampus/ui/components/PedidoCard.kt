package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.theme.ColorEstudiante
import java.util.Locale

@Composable
fun PedidoCard(
    pedidoConDetalle: PedidoConDetalle,
    onClick: () -> Unit,
    onCancelar: (() -> Unit)? = null
) {
    val p = pedidoConDetalle.pedido
    val detalles = pedidoConDetalle.detalles
    val estStr = p.estado.uppercase()
    val esListo = estStr == "LISTO"
    val esRecibido = estStr == "RECIBIDO"

    val estadoEnum = try {
        EstadoPedido.valueOf(estStr)
    } catch (e: Exception) {
        EstadoPedido.RECIBIDO
    }

    val primerServicio = detalles.firstOrNull()?.servicio?.nombre ?: ""

    val resumenDetalle = detalles.joinToString(" + ") { d ->
        val cantFormatted = if (d.detalle.cantidad % 1.0 == 0.0) {
            d.detalle.cantidad.toInt().toString()
        } else {
            d.detalle.cantidad.toString()
        }
        val unStr = if (d.servicio.unidad.equals("KG", ignoreCase = true)) "kg" else "prendas"
        "$cantFormatted $unStr"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = if (esListo) 1.5.dp else 1.dp,
            color = if (esListo) Color(0xFF22C55E) else Color(0xFFF1F5F9)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconoServicio(nombreServicio = primerServicio)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Pedido #${String.format(Locale.US, "%03d", p.id)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(resumenDetalle, fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Text(
                    String.format(Locale.US, "S/ %.2f", p.total),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ColorEstudiante
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EstadoPedidoChip(estado = estadoEnum)

                if (esListo && !p.casillero.isNullOrBlank()) {
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Casillero ${p.casillero}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else if (esRecibido && onCancelar != null) {
                    Text(
                        "Cancelar",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { onCancelar() }
                    )
                } else {
                    Text(
                        "Entrega ${p.fechaEntrega}",
                        fontSize = 11.sp,
                        color = ColorEstudiante,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}