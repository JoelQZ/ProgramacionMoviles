# Registro de Prompts - Mejora de Cancelación con IA

## Prompt 1: Conversión de lista a estado reactivo
- **Solicitud**: Modificar `listaReservas` en `Models.kt` para usar `mutableStateListOf` en lugar de `listOf`.
- **Ajustes**: Se agregó la importación de `androidx.compose.runtime.mutableStateListOf` para permitir la actualización de la UI en tiempo real.

## Prompt 2: Diálogo de confirmación y eliminación en UI
- **Solicitud**: Implementar un `AlertDialog` de confirmación en `ReservasScreen.kt` al presionar el botón de cancelar reserva y actualizar la lógica en `DetailScreen.kt`.
- **Ajustes**: Se integró el estado `reservaACancelar` y la lógica para remover el elemento de `DatosEjemplo.listaReservas` al presionar "Confirmar".