package com.quijada.clinicasalud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quijada.clinicasalud.ui.theme.PurplePrimary
import com.quijada.clinicasalud.ui.theme.SurfaceCard
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: Int,
    val text: String,
    val isUser: Boolean
)

@Composable
fun AIChatScreen() {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = 1,
                    text = "¡Hola! Soy tu asistente médico virtual de Clínica Salud+. ¿En qué te puedo ayudar hoy?\n\nPuedes preguntarme por recomendaciones médicas según tus síntomas o pedirme cancelar una cita confirmada.",
                    isUser = false
                )
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    fun handleSendMessage() {
        val trimmedText = inputText.trim()
        if (trimmedText.isEmpty()) return

        val userMsgId = messages.size + 1
        val userMsg = ChatMessage(id = userMsgId, text = trimmedText, isUser = true)

        val aiResponseText = processUserIntent(trimmedText)
        val aiMsg = ChatMessage(id = userMsgId + 1, text = aiResponseText, isUser = false)

        messages = messages + userMsg + aiMsg
        inputText = ""

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8FF))
    ) {
        // Encabezado con ícono de robot SmartToy y línea estética púrpura
        Surface(
            color = PurplePrimary,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Asistente Robot",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Asistente Virtual IA",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Clínica Salud+ • En línea",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Área desplazable LazyColumn para la lista de mensajes
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageItem(message = message)
            }
        }

        // Campo de texto inferior (OutlinedTextField) con botón de enviar (Send)
        Surface(
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Escribe tu consulta aquí...", color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { handleSendMessage() })
                )

                IconButton(
                    onClick = { handleSendMessage() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            if (!message.isUser) {
                Surface(
                    shape = CircleShape,
                    color = PurplePrimary,
                    modifier = Modifier
                        .padding(end = 8.dp, top = 4.dp)
                        .size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Surface(
                shape = if (message.isUser) {
                    RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                } else {
                    RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                },
                color = if (message.isUser) PurplePrimary else SurfaceCard,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.text,
                        color = if (message.isUser) Color.White else Color(0xFF2C1D42),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

private fun processUserIntent(userText: String): String {
    val text = userText.lowercase()

    return when {
        // Regla 1: Si el usuario menciona "cancela" o "cancelar"
        text.contains("cancela") || text.contains("cancelar") -> {
            val citaConfirmada = Repository.citas.find { it.estado.equals("Confirmada", ignoreCase = true) }
            if (citaConfirmada != null) {
                citaConfirmada.estado = "Cancelada"
                "Se ha cancelado tu cita con el/la ${citaConfirmada.medicoNombre} programada para el ${citaConfirmada.fecha}."
            } else {
                "No encontré ninguna cita activa con estado 'Confirmada' para cancelar."
            }
        }

        // Regla 2: Síntomas como "dolor", "corazón" o "presión"
        text.contains("dolor") || text.contains("corazón") || text.contains("corazon") ||
                text.contains("presión") || text.contains("presion") -> {
            "Te recomiendo la especialidad de Cardiología y agendar una cita con la Dra. Ana Torres."
        }

        // Regla 3: Mención de "niño" o "fiebre"
        text.contains("niño") || text.contains("niña") || text.contains("nino") ||
                text.contains("nina") || text.contains("fiebre") -> {
            "Te recomiendo la especialidad de Pediatría y agendar una cita con el Dr. Luis Vega."
        }

        // Regla 4: Respuesta por defecto con las capacidades del asistente
        else -> {
            "Hola, soy tu asistente médico virtual de Clínica Salud+. Puedo ayudarte a cancelar citas confirmadas o recomendarte un especialista según tus síntomas (por ejemplo: dolor de corazón, presión alta, fiebre o atención para niños). ¿En qué puedo ayudarte?"
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AIChatScreenPreview() {
    AIChatScreen()
}
