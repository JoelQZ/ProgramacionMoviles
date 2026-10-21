# Registro de Prompts - Fase 2 (`con-ia`)

## Commit A: Pedidos atrasados en la bandeja
**Prompt utilizado:**
> Tengo una aplicación Kotlin Android desarrollada con Jetpack Compose, Material 3, arquitectura MVVM, Room y un LavaRepository (interface).
> Necesito implementar la mejora de "Pedidos atrasados" en el perfil de Administrador respetando estrictamente la arquitectura MVVM + Repository.
> 1. Identificar pedidos atrasados (fecha anterior a hoy y estado != ENTREGADO/CANCELADO).
> 2. En BandejaScreen y PedidoCard marcar con rojo los pedidos vencidos.
> 3. En PanelScreen contabilizar los pedidos atrasados en las métricas.

---

## Commit B: Descuento por volumen
**Prompt utilizado:**
> Tengo una aplicación Kotlin Android desarrollada con Jetpack Compose, Material 3, arquitectura MVVM, Room y un LavaRepository (interface).
> Necesito implementar "Descuento por volumen" (10% si supera 5 kg) respetando MVVM + Repository.
> 1. Guardar el descuento en la entidad Pedido/Room.
> 2. Mostrar la alerta y desglose en NuevoPedidoScreen y DetallePedidoScreen.

---

## Commit C: Repetir un pedido
**Prompt utilizado:**
> Tengo una aplicación Kotlin Android desarrollada con Jetpack Compose, Material 3, arquitectura MVVM, Room y un LavaRepository (interface).
> Necesito implementar "Repetir un pedido" desde el historial del estudiante.
> 1. Botón 'Repetir' en las tarjetas de pedidos.
> 2. Navegación a NuevoPedidoScreen precargando las líneas y cantidades del pedido original con los precios vigentes.
> 3. Recolección asíncrona del Flow con `collect` en el ViewModel para actualizar el `MutableStateFlow` de cantidades.