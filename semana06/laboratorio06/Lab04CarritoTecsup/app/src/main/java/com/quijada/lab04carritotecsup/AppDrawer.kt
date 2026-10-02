package com.quijada.lab04carritotecsup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppDrawer(
    destinoSeleccionado: String,
    cantidadFavoritos: Int = 0,
    onDestinoSeleccionado: (String) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFFEDE3F5)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "JQ",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B3892),
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Joel Quijada",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "joel.quijada@tecsup.edu.pe",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            label = { Text("Inicio") },
            selected = destinoSeleccionado == "Inicio",
            onClick = { onDestinoSeleccionado("Inicio") },
            icon = {
                Icon(
                    imageVector = if (destinoSeleccionado == "Inicio") Icons.Default.Home else Icons.Outlined.Home,
                    contentDescription = "Inicio"
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text("Mis pedidos") },
            selected = destinoSeleccionado == "Mis pedidos",
            onClick = { onDestinoSeleccionado("Mis pedidos") },
            icon = {
                Icon(
                    imageVector = if (destinoSeleccionado == "Mis pedidos") Icons.Default.ShoppingBag else Icons.Outlined.ShoppingBag,
                    contentDescription = "Mis pedidos"
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text("Favoritos") },
            selected = destinoSeleccionado == "Favoritos",
            onClick = { onDestinoSeleccionado("Favoritos") },
            icon = {
                BadgedBox(
                    badge = {
                        if (cantidadFavoritos > 0) {
                            Badge(
                                containerColor = Color(0xFF6B3892),
                                contentColor = Color.White
                            ) {
                                Text(cantidadFavoritos.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (destinoSeleccionado == "Favoritos") Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favoritos"
                    )
                }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text("Perfil") },
            selected = destinoSeleccionado == "Perfil",
            onClick = { onDestinoSeleccionado("Perfil") },
            icon = {
                Icon(
                    imageVector = if (destinoSeleccionado == "Perfil") Icons.Default.AccountCircle else Icons.Outlined.AccountCircle,
                    contentDescription = "Perfil"
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text("Cerrar sesión") },
            selected = destinoSeleccionado == "Cerrar sesión",
            onClick = { onDestinoSeleccionado("Cerrar sesión") },
            icon = {
                Icon(
                    imageVector = if (destinoSeleccionado == "Cerrar sesión") Icons.Default.ExitToApp else Icons.Outlined.ExitToApp,
                    contentDescription = "Cerrar sesión"
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}