## Mejora con IA

| Prompt que usé | Qué generó Gemini | Qué acepté o corregí (y por qué) |
| :--- | :--- | :--- |
| "Agrega validación para campos vacíos en PantallaRegistro y un botón de Limpiar para borrar los datos ingresados en Compose." | Generó la función `PantallaRegistro` con campos de texto, un botón "LIMPIAR" y validación para evitar enviar campos vacíos. | **Acepté:** La estructura base con los estados `remember` y el botón de limpiar.<br>**Corregí:** Agregué validación de tipo de dato numérico (`toDoubleOrNull`/`toIntOrNull`) para que no falle al ingresar letras, precisé los mensajes de error y cambié el botón a `OutlinedButton` para darle jerarquía visual secundaria. |
