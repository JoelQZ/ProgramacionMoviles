# CentroTareas — Laboratorio N° 14 (Hilos en Android)

Aplicación Android desarrollada en Kotlin con Jetpack Compose, Material 3, ViewModel, StateFlow y Arquitectura MVVM + Repository para demostrar el comportamiento del hilo principal (UI Thread), congelamiento de interfaz (UI Freeze), corrutinas, control de concurrencia y manejo de excepciones.

---

## Observación del Paso 1: Provocar el Bloqueo del Hilo Principal (UI Freeze)

### (a) ¿Qué ocurre con la UI durante los 4 segundos?
Al hacer clic en el botón **"Tarea larga (MAL)"**, la interfaz de usuario se congela por completo durante 4 segundos. Durante ese tiempo, cualquier intento de interactuar con la pantalla (por ejemplo, pulsar el botón **"Tócame: $contador"**) es completamente ignorado de forma inmediata. Las animaciones y eventos táctiles no responden porque el hilo principal (*main thread* / UI thread) está completamente bloqueado por la llamada síncrona `Thread.sleep(4000)`.

### (b) ¿Por qué no se llega a dibujar "Trabajando..." antes del bloqueo?
Aunque la línea `_mensaje.value = "Trabajando..."` se ejecuta cronológicamente antes que `Thread.sleep(4000)`, el cambio en el `StateFlow` solo programa una recomposición en el hilo principal. Como la ejecución del método `tareaLargaMala()` se realiza de forma síncrona en el hilo principal, la siguiente línea (`Thread.sleep(4000)`) detiene la ejecución de dicho hilo inmediatamente, antes de que el sistema operativo o el motor de Jetpack Compose puedan procesar el evento de recomposición y dibujar el nuevo frame en pantalla. Por lo tanto, el texto `"Trabajando..."` nunca llega a dibujarse. Al despertar el hilo 4 segundos después, la variable cambia a `"Terminó"` y ese es el único estado que la UI llega a renderizar.

### (c) ¿Qué pasaría si la espera fuera de 10 segundos?
Si la operación síncrona bloqueante durara 10 segundos (o más de 5 segundos sin procesar los eventos de la cola de la UI), el sistema operativo Android detectaría que la aplicación no responde a los eventos de entrada del usuario y desplegaría el diálogo de error de sistema **ANR (*Application Not Responding*)**, sugiriendo al usuario forzar el cierre (*Force Close*) o esperar a que la aplicación responda.
