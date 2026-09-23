## Prompt Utilizado para la Generación de la Interfaz

Para construir la interfaz visual y establecer la navegación base con Jetpack Compose, se estructuró el siguiente prompt detallado:

Tengo una aplicación Android en Jetpack Compose. Adjunto imágenes con el diseño visual objetivo para cada pantalla que debo implementar.

Proporciona el código Jetpack Compose completo y funcional, replicando fielmente los colores (esquema morado Purple40), tipografías, espaciados y elevaciones de las imágenes.

Requisitos Funcionales y de Navegación:

Datos de Alumnos: Crea un data class Alumno(val id: Int, val nombre: String, val carrera: String, val correo: String, val facultad: String, val biografia: String) y una lista de prueba con al menos 3 alumnos (ej. Juan León, María García, Carlos Pérez).

Navegación con paso de parámetros: Al hacer clic en un alumno en el Directorio, se debe pasar su ID a la pantalla de Detalle, la cual debe buscar sus datos en la lista y mostrarlos en la UI correspondiente.

Validación de Login: El botón de iniciar sesión solo debe permitir el ingreso si el correo es exactamente "joel.quijada@tecsup.edu.pe" y la contraseña es "123456". Si es incorrecto, muestra un mensaje de error en pantalla (un Text rojo debajo del botón o un Snackbar) y no navegues.

Petición Específica por Pantalla:

1. Pantalla de Login (HomeScreen.kt):

Pantalla de fondo morado.

Tarjeta (Card) blanca central elevada con:

Icono superior circular de birrete académico en tono morado claro.

Título "Portal Académico" y subtítulo "Ingrese sus credenciales para acceder".

OutlinedTextField para "Correo Institucional" con valor "joel.quijada@tecsup.edu.pe" e icono de correo.

OutlinedTextField para "Contraseña" con valor masked (••••••••), icono de candado y toggle de visibilidad.

Botón morado "INICIAR SESIÓN".

Texto clickable "¿Olvidaste tu contraseña?".

2. Pantalla de Bienvenida (AppNavigation.kt / WelcomeScreen.kt):

Fondo morado.

Título grande "Bienvenido, Joel Quijada" y subtítulo "Selecciona una opción para continuar".

Dos tarjetas blancas horizontales con bordes redondeados y sombra. Cada una con un icono en contenedor circular morado claro a la izquierda y una flecha a la derecha:

Tarjeta 1: "Directorio de Alumnos" (Ver y gestionar estudiantes).

Tarjeta 2: "Mi Perfil Académico" (Datos personales y progreso).

Botón inferior "Cerrar Sesión Segura" con texto e icono en naranja/rojo.

3. Pantalla "Directorio de Alumnos" (ListScreen.kt):

TopAppBar morado claro con título "Directorio de Alumnos" y flecha de retorno.

LazyColumn que itere la lista de prueba de la clase Alumno.

Cada ítem es una Card blanca con sombra que muestra: un avatar circular a la izquierda, el nombre y carrera del alumno, y una flecha hacia la derecha. Al hacer clic, navega a DetailScreen pasando el ID.

4. Pantalla "Expediente Académico" (DetailScreen.kt):

TopAppBar morado con título "Expediente Académico" y flecha de retorno.

Cabecera visual: Un bloque superior morado con bordes inferiores curvos (RoundedCornerShape).

Avatar superpuesto: Un avatar circular (foto o icono de usuario) centrado que se superponga entre el fondo morado y el fondo blanco inferior.

Debajo del avatar: Nombre y carrera del alumno seleccionado (dinámico según el ID recibido).

Tarjeta de información (Card): Una tarjeta central blanca elevada que muestre los datos del alumno:

ID Estudiante, Correo Electrónico y Facultad (con pequeños iconos a la izquierda).

Sección "Biografía" con la descripción del alumno.

5. Pantalla "Configuración de Perfil" (ProfileScreen.kt):

TopAppBar morado con título "Configuración de Perfil" y flecha de retorno.

Encabezado superior morado con forma curva inferior. Avatar circular centrado superpuesto.

Debajo del avatar: Nombre "Joel Quijada" en negrita y subtítulo "Perfil Estudiantil".

Tarjeta central blanca elevada con los siguientes datos estáticos (con iconos morados circulares a la izquierda):

Nombre Completo: Joel Quijada

Correo: joel.quijada@tecsup.edu.pe

Teléfono: +51 987 654 321

Carrera: Diseño y desarrollo de software

Ciclo Actual: 4to Ciclo

Botón inferior tipo OutlinedButton con borde rojo, texto "Cerrar Sesión" e icono rojo.

Genera el código Jetpack Compose limpio, sin comentarios excesivos, utilizando MaterialTheme y estructurado correctamente para que la navegación y la UI funcionen de inmediato.