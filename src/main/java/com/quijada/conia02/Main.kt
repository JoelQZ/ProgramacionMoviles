package com.quijada.conia02

/**
 * 1. Abstracción: Clase abstracta que define la estructura base de un Producto.
 */
abstract class Producto(
    val id: Int,
    val nombre: String,
    val precioBase: Double
) {
    abstract fun calcularPrecioFinal(): Double
}

/**
 * 2. Herencia: ProductoFisico extiende de Producto y añade costo de envío.
 */
class ProductoFisico(
    id: Int,
    nombre: String,
    precioBase: Double,
    private val costoEnvio: Double
) : Producto(id, nombre, precioBase) {
    override fun calcularPrecioFinal(): Double = precioBase + costoEnvio

    override fun toString(): String = "Físico - $nombre (ID: $id) | Base: $precioBase | Envío: $costoEnvio | Final: ${calcularPrecioFinal()}"
}

/**
 * 2. Herencia: ProductoDigital extiende de Producto y aplica un descuento.
 */
class ProductoDigital(
    id: Int,
    nombre: String,
    precioBase: Double,
    private val porcentajeDescuento: Double
) : Producto(id, nombre, precioBase) {
    override fun calcularPrecioFinal(): Double {
        val descuento = precioBase * (porcentajeDescuento / 100)
        return precioBase - descuento
    }

    override fun toString(): String = "Digital - $nombre (ID: $id) | Base: $precioBase | Desc: $porcentajeDescuento% | Final: ${calcularPrecioFinal()}"
}

/**
 * 3. Encapsulamiento: La lista de productos es privada y se accede vía métodos públicos.
 */
class Carrito {
    private val listaProductos = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        listaProductos.add(producto)
        println("Producto agregado: ${producto.nombre}")
    }

    // 5. Operaciones de negocio: buscarProducto
    fun buscarProducto(id: Int): Producto? {
        return listaProductos.find { it.id == id }
    }

    // 5. Operaciones de negocio: eliminarProducto usando removeIf
    fun eliminarProducto(id: Int): Boolean {
        return listaProductos.removeIf { it.id == id }
    }

    // 4. Polimorfismo: calcularSubtotal usa calcularPrecioFinal() dinámicamente según el tipo de producto
    fun calcularSubtotal(): Double {
        return listaProductos.sumOf { it.calcularPrecioFinal() }
    }

    fun calcularIGV(): Double {
        return calcularSubtotal() * 0.18
    }

    fun calcularTotal(): Double {
        return calcularSubtotal() + calcularIGV()
    }

    fun mostrarCarrito() {
        if (listaProductos.isEmpty()) {
            println("\nEl carrito está vacío.")
        } else {
            println("\n--- Contenido del Carrito ---")
            listaProductos.forEach { println(it) }
        }
    }
}

/**
 * 6. Función main(): Punto de entrada para la ejecución en consola.
 */
fun main() {
    val miCarrito = Carrito()

    // Instanciar productos
    val laptop = ProductoFisico(1, "Laptop Gamer", 2500.0, 50.0)
    val libroFisico = ProductoFisico(2, "Kotlin in Action", 120.0, 15.0)
    val cursoKotlin = ProductoDigital(3, "Master en Android", 300.0, 20.0) // 20% descuento
    val ebook = ProductoDigital(4, "Arquitectura Limpia", 80.0, 10.0) // 10% descuento

    // Agregar productos al carrito
    println("--- Agregando productos ---")
    miCarrito.agregarProducto(laptop)
    miCarrito.agregarProducto(libroFisico)
    miCarrito.agregarProducto(cursoKotlin)
    miCarrito.agregarProducto(ebook)

    // Mostrar carrito inicial
    miCarrito.mostrarCarrito()

    // Buscar producto
    println("\n--- Buscando producto con ID 3 ---")
    val encontrado = miCarrito.buscarProducto(3)
    println(encontrado ?: "Producto no encontrado")

    // Eliminar producto
    println("\n--- Eliminando producto con ID 2 (Libro Físico) ---")
    val eliminado = miCarrito.eliminarProducto(2)
    if (eliminado) println("Producto eliminado correctamente.")

    // Resumen de compra final
    println("\n========================================")
    println("          RESUMEN DE COMPRA")
    println("========================================")
    miCarrito.mostrarCarrito()
    
    val subtotal = miCarrito.calcularSubtotal()
    val igv = miCarrito.calcularIGV()
    val total = miCarrito.calcularTotal()

    println("----------------------------------------")
    println("Subtotal (inc. envío/desc): S/ %.2f".format(subtotal))
    println("IGV (18%%):                 S/ %.2f".format(igv))
    println("TOTAL FINAL:               S/ %.2f".format(total))
    println("========================================")
}
