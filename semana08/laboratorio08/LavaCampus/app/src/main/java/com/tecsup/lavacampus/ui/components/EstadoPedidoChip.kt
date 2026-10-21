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
        EstadoPedido.RECIBIDO -> Triple(Color(0xFFFEF3C7), "Recibido", Color(0xFFD97706))
        EstadoPedido.EN_PROCESO -> Triple(Color(0xFFEEF2FF), "En proceso", Color(0xFF4F46E5))
        EstadoPedido.LISTO -> Triple(Color(0xFFDCFCE7), "Listo", Color(0xFF16A34A))
        EstadoPedido.ENTREGADO -> Triple(Color(0xFFF1F5F9), "Entregado", Color(0xFF64748B))
        EstadoPedido.CANCELADO -> Triple(Color(0xFFFEE2E2), "Cancelado", Color(0xFFDC2626))
    }

    Text(
        text = texto,
        color = colorTexto,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}