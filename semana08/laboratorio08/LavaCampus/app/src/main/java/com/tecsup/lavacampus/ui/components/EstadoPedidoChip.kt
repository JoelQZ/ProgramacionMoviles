package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.EstadoPedido

@Composable
fun EstadoPedidoChip(
    estado: EstadoPedido,
    modifier: Modifier = Modifier
) {
    val (fondo, texto, colorTexto) = when (estado) {
        EstadoPedido.RECIBIDO -> Triple(Color(0xFFE0F2FE), "Recibido", Color(0xFF0284C7))
        EstadoPedido.EN_PROCESO -> Triple(Color(0xFFE0F2FE), "En proceso", Color(0xFF0284C7))
        EstadoPedido.LISTO -> Triple(Color(0xFFDCFCE7), "Listo", Color(0xFF16A34A))
        EstadoPedido.ENTREGADO -> Triple(Color(0xFFF3F4F6), "Entregado", Color(0xFF4B5563))
        EstadoPedido.CANCELADO -> Triple(Color(0xFFFEE2E2), "Cancelado", Color(0xFFDC2626))
    }

    Text(
        text = texto,
        color = colorTexto,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}