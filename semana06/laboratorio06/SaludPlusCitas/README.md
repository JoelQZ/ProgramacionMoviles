# Informe del Proyecto: SaludPlus

---

## VII. Preguntas de Reflexión

> **1. ¿Por qué los modelos, `Rutas.kt` y `AppNavigation.kt` se entregaron completos y las pantallas no? ¿Qué tienen en común los archivos que sí se dejaron como esqueleto?**
>
> **Respuesta:**
> Los modelos y la navegación definen la arquitectura base y contratos del proyecto. Los archivos dejados como esqueleto tienen en común que forman la capa de interfaz de usuario (UI), cuyo objetivo en la práctica era desarrollar las vistas e interacciones usando Jetpack Compose.

---

> **2. ¿Por qué el `Repositorio` es un `object` y no una clase normal? ¿Qué pasaría con las citas si cada pantalla creara su propia lista?**
>
> **Respuesta:**
> Se define como `object` para implementar el patrón Singleton, garantizando una única fuente de datos en toda la app. Si fuera una clase normal, cada pantalla crearía una instancia independiente en memoria, perdiendo los datos y las citas creadas al cambiar de vista.

---

> **3. ¿Cómo lograste que la búsqueda de especialidades y los horarios disponibles se actualicen solos, sin que tú "actualices" nada a mano?**
>
> **Respuesta:**
> Se logró mediante el estado reactivo de Jetpack Compose (`remember` y `mutableStateOf`). Al modificar un campo o filtro, Compose detecta el cambio de estado y dispara la recomposición automática de la interfaz.

---

> **4. ¿Qué diferencia notaste entre `navigate()` normal (`Especialidades` → `Médicos`) y el que usa `popUpTo` (`Confirmar cita` → `Cita agendada`)?? ¿Qué pasa al presionar Atrás en cada caso?**
>
> **Respuesta:**
> * **`navigate()` normal:** Apila las pantallas en el *backstack*. Al presionar *Atrás*, regresa paso a paso a la pantalla anterior.
> * **`popUpTo`:** Elimina las pantallas indicadas de la pila. Al presionar *Atrás* después de agendar, evita volver a la confirmación e impide duplicar reservas.

---

> **5. ¿Qué tuviste que corregir del código que te generó la IA para el calendario dinámico?**
>
> **Respuesta:**
> Se corrigió el manejo del estado de selección de fechas y la compatibilidad con los formatos de fecha (`LocalDate`) para asegurar que la vista actualice correctamente la opción seleccionada sin perder datos en la recomposición.

---

> **6. Compara el `NavigationDrawer` del Laboratorio 6 con el `NavigationBar` de esta tarea: ¿en qué caso usarías cada uno en un proyecto propio?**
>
> **Respuesta:**
>
> | Componente | Caso de uso ideal | Ejemplos de uso |
> | :--- | :--- | :--- |
> | **`NavigationBar` (Inferior)** | Secciones principales de alta frecuencia de uso (3 a 5 items). | *Inicio, Citas, Perfil* |
> | **`NavigationDrawer` (Lateral)** | Menús secundarios o apps complejas con muchas opciones. | *Ajustes, Ayuda, Historial, Cerrar Sesión* |

---

## VIII. Observaciones y Conclusiones

### Observaciones
* **Manejo de estado en Registro/Login:** Durante la integración fue clave asegurar que los datos del formulario se enviaran al `Repositorio` antes de navegar, evitando errores de credenciales nulas.
* **Gestión del backstack:** Se identificó que al confirmar una cita es indispensable usar `popUpTo` con `inclusive = true`, previniendo que el usuario regrese a la pantalla de confirmación y duplique registros de forma accidental.

### Conclusiones
* **Desarrollo ágil sobre esqueletos:** Trabajar desde una arquitectura predefinida (Fase 2) permite enfocarse directamente en la experiencia de usuario y la interfaz (UI), a diferencia de la Fase 1 donde se requiere más tiempo para estructurar la base del proyecto.
* **Arquitectura modular y reactiva:** La separación entre datos (`Repositorio`), navegación (`AppNavigation`) y vistas demostró la potencia de Jetpack Compose: la interfaz se actualiza de forma fluida y automática según el estado de la aplicación.