# Registro de Prompts y Criterio Técnico (Fase 2 · con-ia)

Este documento registra las interacciones con la Inteligencia Artificial (Gemini) durante la implementación de la **Fase 2 (con-ia)** de la aplicación **CampusGPS**. Se aplicaron las 6 mejoras avanzadas del laboratorio utilizando arquitectura **MVVM + Repository**, programación reactiva con **Kotlin Flow**, persistencia con **Jetpack DataStore Preferences** e integración con **osmdroid**.

Durante todo el proceso se mantuvo un criterio técnico riguroso:
- La UI (`Composables`) se limita a observar estados (`StateFlow`) y delegar eventos.
- La lógica de negocio, ordenamiento por distancia y filtrado se concentra en `MapaViewModel`.
- El `ViewModel` jamás importa `android.content.Context` ni dependencias de osmdroid.
- Los sensores GPS, Google Play Services y la persistencia DataStore se encapsulan en sus respectivos repositorios.

---

## Registro de Prompts y Decisiones de Diseño

| Prompt que escribí | Qué respondió Gemini | Qué cambié o descarté | Por qué |
| :--- | :--- | :--- | :--- |
| **1. Filtro por categoría:**<br>`"Agrega un filtro por categoría (Todos, Estudio, Comida, Eventos, Servicios) en la UI y en el ViewModel sin romper la arquitectura MVVM."` | Propuso un `MutableStateFlow<String?>` para la categoría seleccionada en `MapaViewModel` y combinarlo con la ubicación y lista de lugares mediante `combine()`. En la UI sugirió una fila horizontal con `FilterChip`. | Acepté la solución reactiva con `combine()`. Me aseguré de que el filtrado ocurriera en el `ViewModel` y no mediante un `filter` dentro de `ListaLugares.kt`. | Respetar el patrón MVVM: la vista solo renderiza datos procesados y el ViewModel expone un único `StateFlow<List<LugarConDistancia>>` filtrado y ordenado. |
| **2. Radio de cercanía y overlay:**<br>`"Añade un selector de radio (100m, 200m, 500m) que marque en la lista qué lugares están en radio y dibuje un círculo en el mapa de osmdroid."` | Sugirió exponer `radioSeleccionado` en `MapaViewModel`, calcular el atributo booleano `dentroDelRadio` en el modelo y dibujar un `Polygon.pointsAsCircle` en `MapaUbicacion.kt`. | Reordené la adición de capas en osmdroid para que el `Polygon` se agregue antes que los marcadores (`Overlay`). | Si el polígono se agrega después de los marcadores, intercepta los eventos de toque y bloquea el clic en los marcadores de los lugares. |
| **3. Persistencia con DataStore:**<br>`"Implementa Jetpack DataStore Preferences para guardar el ID del último lugar visitado y restaurarlo al abrir la app."` | Generó `PreferenciasRepository` y `PreferenciasRepositoryDataStore`, recomendando inyectar `Context` en el `ViewModel` para acceder al DataStore. | **Descarté** la inyección de `Context` en `MapaViewModel`. Inyecté `PreferenciasRepository` a través de `CampusViewModelFactory` pasando `applicationContext` únicamente en la capa `Data`. | Mantener `MapaViewModel` 100% libre de `Context` para prevenir fugas de memoria (*memory leaks*) y facilitar pruebas unitarias. |
| **4. Punto azul nativo y centrado:**<br>`"Agrega la capa MyLocationNewOverlay de osmdroid para mostrar la posición actual en azul y un botón flotante para centrar el mapa."` | Proporcionó el código para instanciar `MyLocationNewOverlay` activando `enableMyLocation()` y `enableFollowLocation()`, junto con un `FloatingActionButton` en Compose. | Condicioné la activación de `MyLocationNewOverlay` a la bandera `permisoConcedido`. Si no hay permiso, la capa se desactiva y oculta de forma segura. | Evitar excepciones `SecurityException` en runtime y crashes si el usuario revoca los permisos de ubicación mientras la app está abierta. |
| **5. Seguimiento en vivo:**<br>`"Implementa un modo de seguimiento en tiempo real usando callbackFlow de Kotlin Coroutines en el repositorio y un botón En vivo en la UI."` | Diseñó `observarUbicacionEnVivo(intervaloMs)` en `UbicacionRepositoryGoogle` usando `fusedLocationClient.requestLocationUpdates` dentro de un `callbackFlow` finalizado con `awaitClose`. En `MapaViewModel` ofreció un `Job` para iniciar/detener el flujo. | Ajusté la gestión del `Job` de corrutina en `MapaViewModel` para cancelarlo explícitamente en `onCleared()` y al pausar la app. | Garantizar que el GPS no continúe consumiendo batería en segundo plano al cerrar o minimizar la aplicación. |
| **6. Modo oscuro y paleta Azul Noche:**<br>`"Adapta el mapa a modo oscuro con osmdroid TilesOverlay.INVERT_COLORS y aplica la paleta Azul Noche (#0F172A / #38BDF8) en la UI."` | Explicó cómo aplicar el filtro de inversión de color a las baldosas del mapa y propuso una paleta de colores para Compose con un conmutador de tema en la cabecera. | Corregí la barra de estado con `enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(TRANSPARENT))` y `WindowInsets(0,0,0,0)` para que la cabecera cubra desde el píxel `0` superior sin franjas blancas detrás de la hora. | Lograr una integración Edge-to-Edge nativa e inmersiva sin superposiciones ni franjas blancas en dispositivos Android modernos. |
| **7. Fallback GPS para dispositivos físicos:**<br>`"Refuerza obtenerUbicacionActual() para que no retorne null bajo techo en celulares físicos sin alterar el ViewModel."` | Propuso reemplazar la consulta única por una secuencia envuelta en `withTimeoutOrNull(8000L)`: `getCurrentLocation(BALANCED_POWER_ACCURACY)` → `lastLocation` → `requestLocationUpdates` con `suspendCancellableCoroutine`. | Implementé la secuencia completa directamente en `UbicacionRepositoryGoogle.kt` sin tocar `MapaViewModel` ni la interfaz `UbicacionRepository`. | Resolver la falta de señal GPS en interiores en hardware real sin contaminar la arquitectura ni alterar las firmas de la capa de presentación. |

---

## Auditoría de Arquitectura MVVM

Se verificó el cumplimiento de las reglas de arquitectura en la solución final:

1. **Inyección de Dependencias Limpia:**
   - `MapaViewModel` recibe únicamente interfaces abstractions: `UbicacionRepository`, `LugaresRepository` y `PreferenciasRepository`.
   - La instanciación de las implementaciones concretas (`UbicacionRepositoryGoogle`, `LugaresRepositoryLocal`, `PreferenciasRepositoryDataStore`) ocurre exclusivamente en `MainActivity.kt` mediante `CampusViewModelFactory`.

2. **Independencia del Framework Android en la Capa ViewModel:**
   - No existen importaciones de `android.content.Context`, `android.location.Location`, ni librerías de UI como `org.osmdroid` en `MapaViewModel.kt`.
   - La capa de presentación (`PantallaMapa.kt`, `ListaLugares.kt`, `MapaUbicacion.kt`) no realiza cálculos de distancia ni lógica de filtrado; únicamente observa los `StateFlow` expuestos.

3. **Seguridad y Configuración:**
   - El archivo `local.properties` donde se almacena la API Key de Google Maps se encuentra debidamente ignorado en `.gitignore` para evitar filtrados en el repositorio de control de versiones.
