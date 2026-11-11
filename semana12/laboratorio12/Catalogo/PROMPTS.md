# Registro de Prompts - Fase 2 (con-ia)

Este documento registra los prompts utilizados con los asistentes de IA para el desarrollo de las pruebas unitarias y las mejoras de UI/UX en la rama `con-ia`.

---

## Bloque 1: Pruebas Unitarias para ListaViewModel (Requisito R12)

**Objetivo:** Configurar dependencias de testing (MockK, Coroutines Test) e implementar 3 pruebas unitarias obligatorias.

**Prompt utilizado:**
> Necesito implementar las 3 pruebas unitarias obligatorias (Requisito R12) para `ListaViewModel` en mi proyecto Android Jetpack Compose usando MockK y Kotlin Coroutines.
>
> 1. Primero, revisa si mi `app/build.gradle.kts` tiene las dependencias necesarias de testing. Si faltan, agrégalas:
     >    - org.jetbrains.kotlinx:kotlinx-coroutines-test
>    - io.mockk:mockk
>    - androidx.arch.core:core-testing (o JUnit 4)
>
> 2. Crea o edita el archivo de prueba unitaria en la ruta `app/src/test/java/com/tecsup/catalogo/ui/lista/ListaViewModelTest.kt`.
>
> 3. Implementa las siguientes 3 pruebas con @Test usando `MainDispatcherRule` / `UnconfinedTestDispatcher` para manejar el coroutine dispatcher de Main:
     >    - `cargarItems_exito`: Verifica que al inicializar el ViewModel, `uiState` pasa de Cargando a Exito con la lista de items devuelta por el `ItemRepository` mockeado.
>    - `cargarItems_error`: Verifica que si el `ItemRepository` lanza una excepción/error, el `uiState` del ViewModel cambia a `UiState.Error`.
>    - `toggleFavorito_llamaRepository`: Verifica que al llamar a `toggleFavorito(item)`, se invoque correctamente el método correspondiente en el `ItemRepository` y se actualice el estado.
>
> Por favor, genera el código completo con todos los imports necesarios y mantén la arquitectura Clean/MVVM del proyecto.

---

## Bloque 2: Funcionalidad de Buscador en Tiempo Real

**Objetivo:** Agregar campo de búsqueda en la pantalla principal para filtrar los smartphones por nombre o marca.

**Prompt utilizado:**
> Quiero implementar la mejora con IA para la pantalla principal de mi catálogo (Bloque 2).
>
> Por favor realiza los siguientes cambios visibles en la aplicación:
>
> 1. En `ListaViewModel.kt`:
     >    - Agrega un estado para el texto de búsqueda (`queryBusqueda` con `StateFlow` o `mutableStateOf`).
>    - Implementa una función `onBusquedaChanged(nuevaQuery: String)` para actualizar el término de búsqueda.
>    - Filtra la lista de smartphones devuelta para que solo muestre los ítems cuyo nombre o marca contengan la búsqueda introducida (sin importar mayúsculas/minúsculas).
>
> 2. En `ListaScreen.kt`:
     >    - Añade un campo de texto `OutlinedTextField` en la parte superior (debajo del TopAppBar) con un icono de lupa (Search) y un botón de limpiar (Clear/Close).
>    - Conecta este campo con la función `onBusquedaChanged` del ViewModel para que la lista se filtre automáticamente en tiempo real mientras el usuario escribe.
>    - Si la búsqueda no produce ningún resultado, muestra un mensaje amigable: "No se encontraron smartphones con ese término".
>    - Mejora el diseño de la tarjeta del ítem (`Card`) agregando esquinas redondeadas, elevación con sombra suave y un badge visual que resalte la marca.
>
> Por favor genera e integra el código asegurando que compile sin errores.

---

## Bloque 3: Rediseño UI/UX Cyberpunk Neón

**Objetivo:** Aplicar un tema visual futurista y moderno con colores vivos e intensos en toda la aplicación.

**Prompt utilizado:**
> Quiero realizar un rediseño UI/UX completo para toda la aplicación (`Theme`, `LoginScreen`, `ListaScreen`, `DetalleScreen`, `AdminScreen` y `FavoritosScreen`) adoptando un estilo futurista, moderno y tecnológico, utilizando una paleta de colores vivos y contrastados (Cyberpunk / Tech Neón), dejando de lado los tonos pálidos o apagados.
>
> Por favor, realiza las siguientes modificaciones globales y específicas:
>
> 1. Configuración de Tema y Colores (Color.kt / Theme.kt):
     >    - Define una paleta de colores vivos y saturados:
            >      * Color Primario: Azul Eléctrico / Violeta Neón (ej. #6C5CE7 o #4834D4).
>      * Color Secundario / Acento: Neón Magenta / Cían Eléctrico (ej. #00CECB o #FF007F).
>      * Superficies / Tarjetas: Gradientes oscuros o contenedores con fondo azul noche/violeta profundo (ej. #1E1B4B o #0F172A) con bordes brillantes/neón.
>      * Texto e Iconos: Blanco puro (#FFFFFF) y acentos Neón para asegurar alto contraste y legibilidad.
>    - Aplica este tema globalmente en `CatalogoTheme`.
>
> 2. Rediseño de Pantallas:
     >    - LoginScreen:
            >      * Agrega un encabezado impactante con tipografía moderna y fondo con acentos neón.
>      * Botones principales con colores vivos (Magenta/Azul Neón) y bordes muy marcados.
>      * Campos de texto (OutlinedTextField) con bordes neón al enfocar.
>    - ListaScreen & FavoritosScreen:
       >      * Rediseña las tarjetas (`Card`) con bordes de color neón/acento, elevación y esquinas redondeadas modernas (16.dp o 20.dp).
>      * Los chips/badges de marca y precio deben usar contenedores de colores muy vivos con texto blanco contrastado.
>      * Los iconos (corazón de favoritos, botones del topbar) deben resaltar con color magenta o rojo encendido.
>    - DetalleScreen:
       >      * Las cajas de 'Rating' y 'Stock' deben destacar como módulos futuristas con fondos saturados (ej. cían brillante o púrpura neón).
>      * La imagen del producto debe incluir una sombra o brillo (glow/neon) suave alrededor de la tarjeta.
>    - AdminScreen:
       >      * Las tarjetas métricas ('Guardados', 'Favoritos') deben convertirse en marcadores futuristas con números grandes en color cían/neón y fondo oscuro contrastante.
>      * Los botones 'Sincronizar' y 'Limpiar caché' deben usar gradientes o colores primarios vivos.
>
> Asegúrate de que el código compile sin errores y mantiene la funcionalidad de la app intacta.