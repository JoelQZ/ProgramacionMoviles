¿Por qué nombre y precio son val pero cantidad es var? 
nombre y precio usan val porque son atributos inmutables del producto que no deben cambiar durante la transicion en cambio cantidad usar var porque es un valor que el cliente puede modificar al añadir o quitar unidades del mismo producto.

¿Qué pasaría si intentas cambiar el precio después de crear el producto?
El programa se detiene con un error porque no se puede modificar variables de tipo val, asi evitando que alguien modifique el precio.