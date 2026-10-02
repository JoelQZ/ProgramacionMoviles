package com.quijada.lab04carritotecsup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTienda() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var destinoSeleccionado by remember { mutableStateOf("Inicio") }

    val listaProductosBase = remember {
        listOf(
            Producto("Audifonos", 89.00),
            Producto("Smartwatch", 199.00),
            Producto("Funda celular", 25.00)
        )
    }

    val listaFavoritos = remember { mutableStateListOf<Producto>() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                destinoSeleccionado = destinoSeleccionado,
                cantidadFavoritos = listaFavoritos.size,
                onDestinoSeleccionado = { opcion ->
                    destinoSeleccionado = opcion
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (destinoSeleccionado == "Mis pedidos") "TECSUP Store" else destinoSeleccionado,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (destinoSeleccionado == "Mis pedidos") {
                                Text(
                                    text = "Más vendidos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFEDE3F5)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6B3892)
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (destinoSeleccionado) {
                    "Inicio" -> {
                        PantallaCarrito()
                    }

                    "Mis pedidos" -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp)
                        ) {
                            items(listaProductosBase) { producto ->
                                TarjetaProducto(
                                    producto = producto,
                                    onFavoritoClick = {
                                        if (!listaFavoritos.contains(producto)) {
                                            listaFavoritos.add(producto)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    "Favoritos" -> {
                        if (listaFavoritos.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No tienes productos en favoritos",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 8.dp)
                            ) {
                                items(listaFavoritos) { producto ->
                                    TarjetaProducto(
                                        producto = producto,
                                        onFavoritoClick = { }
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Pantalla de $destinoSeleccionado",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}