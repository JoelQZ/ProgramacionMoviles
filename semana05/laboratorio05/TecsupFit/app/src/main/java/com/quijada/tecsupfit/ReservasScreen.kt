package com.quijada.tecsupfit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReservasScreen() {
    var reservaACancelar by remember { mutableStateOf<Reserva?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Text(
            text = "Mis reservas",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (DatosEjemplo.listaReservas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes reservas activas",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = DatosEjemplo.listaReservas,
                    key = { it.id }
                ) { reserva ->
                    ItemReserva(
                        reserva = reserva,
                        onCancelarClick = { reservaACancelar = reserva }
                    )
                }
            }
        }
    }

    if (reservaACancelar != null) {
        val reserva = reservaACancelar!!
        AlertDialog(
            onDismissRequest = { reservaACancelar = null },
            title = {
                Text(
                    text = "Cancelar reserva",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = "¿Estás seguro de que deseas cancelar la reserva de \"${reserva.nombreClase}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        DatosEjemplo.listaReservas.remove(reserva)
                        reservaACancelar = null
                    }
                ) {
                    Text(
                        text = "Confirmar",
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { reservaACancelar = null }
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}

@Composable
fun ItemReserva(
    reserva: Reserva,
    onCancelarClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(60.dp)
                        .background(
                            if (reserva.estado == "Confirmada") Color(0xFF00695C) else Color.Gray,
                            RoundedCornerShape(2.dp)
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reserva.nombreClase,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = reserva.fechaHora,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val esConfirmada = reserva.estado == "Confirmada"
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(
                                text = reserva.estado,
                                fontSize = 11.sp,
                                color = if (esConfirmada) Color(0xFF00695C) else Color.DarkGray
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (esConfirmada) Color(0xFFE0F2F1) else Color(0xFFEEEEEE)
                        ),
                        border = null
                    )
                }
            }

            if (reserva.estado == "Confirmada") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onCancelarClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFD32F2F))
                ) {
                    Text(
                        text = "Cancelar Reserva",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}