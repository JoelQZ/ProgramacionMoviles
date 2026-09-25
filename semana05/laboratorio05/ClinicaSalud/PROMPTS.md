# Documentación de Prompts e Interacciones con IA

## Proyecto: Clínica Salud+
**Alumno:** Joel Quijada  
**Rama:** `con-ia`

---

## Commit 1: Sincronización Base del Proyecto
* **Descripción:** Restauración y sincronización completa de la estructura de archivos, recursos Jetpack Compose y dependencias Gradle desde la rama `sin-ia`.

---

## Commit 2: Creación de la Pantalla de Asistente Conversacional (`AIChatScreen.kt`)

### Prompt 2.1: Generación de la Interfaz y Lógica del Chatbot
* **Solicitud (Prompt):**
  > "Actúa como un desarrollador experto en Jetpack Compose y Android en Kotlin. Genera el código para una pantalla llamada `AIChatScreen.kt` en el paquete `com.quijada.clinicasalud`. Debe usar la paleta `PurplePrimary` (#5A3185), tarjetas de mensaje `SurfaceCard`, un encabezado con ícono `SmartToy`, lista desplazable (`LazyColumn`) y campo de envío. Debe incluir lógica NLP simulada para recomendar especialistas por síntomas y cancelar citas cambiando su estado en `Repository.citas`."
* **Respuesta de la IA:** Se obtuvo el Composable `AIChatScreen` con la estructura de chat de doble burbuja y la función `processAIIntent` vinculada al repositorio.
* **Ajuste / Corrección realizada:** Se validaron las importaciones del tema de color institucional (`PurplePrimary`) y la mutabilidad en tiempo real sobre la lista `Repository.citas`.

---

## Commit 3: Integración de Navegación, Funcionalidad de Cancelación y Finalización

### Prompt 3.1: Integración del Chatbot en la Navegación Principal
* **Solicitud (Prompt):**
  > "Integra `AIChatScreen` dentro del flujo de navegación en Jetpack Compose. Agrega la ruta `'ai_chat'` en `AppNavigation.kt` y suma el ítem 'Asistente IA' con el ícono `SmartToy` en el menú lateral desplegable (`MainDrawerScreen.kt`)."
* **Respuesta de la IA:** Código de rutas actualizado para renderizar la pantalla del chatbot y manejar el cierre automático del Drawer al seleccionar la opción.
* **Ajuste / Corrección realizada:** Se ajustaron los parámetros del `NavController` para que el flujo retorne correctamente a la pantalla principal.

### Prompt 3.2: Eliminación Interactiva de Citas en `AppointmentsScreen.kt`
* **Solicitud (Prompt):**
  > "Modifica `AppointmentsScreen.kt` para que al presionar el botón 'Cancelar Cita', la tarjeta se elimine directamente del `Repository.citas` usando `SnapshotStateList`, provocando que la cita desaparezca/se borre al instante de la interfaz."
* **Respuesta de la IA:** Se actualizó la lógica del botón para remover el objeto cita mediante `Repository.citas.remove(cita)`.
* **Ajuste / Corrección realizada:** Se confirmó el re-renderizado instantáneo de la lista al presionar 'Cancelar Cita' en la vista 'Mis Citas'.