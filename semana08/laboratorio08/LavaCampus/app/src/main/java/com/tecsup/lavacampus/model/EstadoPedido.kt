package com.tecsup.lavacampus.model

import androidx.compose.ui.graphics.Color

enum class EstadoPedido(val titulo: String, val color: Color) {
    RECIBIDO("Recibido", Color(0xFFEA580C)),
    EN_PROCESO("En proceso", Color(0xFF2563EB)),
    LISTO("Listo", Color(0xFF16A34A)),
    ENTREGADO("Entregado", Color(0xFF475569)),
    CANCELADO("Cancelado", Color(0xFFDC2626))
}