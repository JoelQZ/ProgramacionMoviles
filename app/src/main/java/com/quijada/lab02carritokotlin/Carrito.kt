package com.quijada.lab02carritokotlin

data class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)

fun calcularSubtotal(productos: List<Producto>): Double{
    var subtotal = 0.0
    for (p in productos){
        subtotal += p.precio * p.cantidad
    }
    return subtotal
}

fun calcularIGV(subtotal: Double): Double{
    return subtotal * 0.18
}

fun calcularTotal(subtotal: Double, igv: Double): Double{
    return subtotal + igv
}

fun calcularDescuento(total: Double): Double{
    return when{
        total > 5000 -> total * 0.10
        total > 3000 -> total * 0.05
        else -> 0.0
    }
}
fun mostrarDetalle(productos: List<Producto>){
    println("--------- DETALLE DEL CARRITO ---------")
    var i = 1
    for (p in productos){
        val importe = p.precio * p.cantidad
        println(String.format("%d. %-20s x%d S/ %8.2f", i, p.nombre, p.cantidad, importe))
        i++
    }
    println("---------------------------------------")
}

fun buscarProducto(productos: List<Producto>, nombre: String): Producto?{
    return productos.find { it.nombre.equals(nombre, ignoreCase = true) }
}

fun main(){
    println("=========================================")
    println(" CARRITO DE COMPRAS - TIENDA TECSUP ")
    println("=========================================")

    val nombreCliente = "Joel Quijada"
    val carrito = mutableListOf<Producto>()

    println("Cliente: $nombreCliente")
    println()

    carrito.add(Producto("Laptop Asus", 3600.0, 1))
    carrito.add(Producto("Teclado Aula", 200.5, 2))
    carrito.add(Producto("Mouse Razer", 499.9, 1))
    carrito.add(Producto("Mouse Pad", 59.90, 3))

    mostrarDetalle(carrito)
    println("Cantidad de productos: ${carrito.size}")

    val masCaro = carrito.maxByOrNull { it.precio }
    if (masCaro != null){
        println("Producto mas caro: ${masCaro.nombre} " + String.format("(S/ %.2f)", masCaro.precio))
    }
    println()

    val buscado = buscarProducto(carrito, "Teclado Aula")
    if (buscado != null){
        println("Producto encontrado: ${buscado.nombre}")
    } else {
        println("Producto no encontrado")
    }

    println("Eliminando Mouse Pad del carrito")
    carrito.removeIf { it.nombre.equals("Mouse Pad", ignoreCase = true) }
    println()

    mostrarDetalle(carrito)

    val subtotal = calcularSubtotal(carrito)
    val igv = calcularIGV(subtotal)
    val total = calcularTotal(subtotal,igv)
    val descuento = calcularDescuento(total)
    val totalFinal = total - descuento

    println()
    println(String.format("%-20s: S/ %8.2f","Subtotal", subtotal))
    println(String.format("%-20s: S/ %8.2f","IGV (18%)", igv))
    println(String.format("%-20s: S/ %8.2f","TOTAL", total))
    println(String.format("%-20s: S/ %8.2f","Descuento", descuento))
    println(String.format("%-20s: S/ %8.2f","TOTAL A PAGAR", totalFinal))
}