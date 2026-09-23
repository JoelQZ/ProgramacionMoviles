package com.quijada.navlab.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Welcome : Screen("welcome")
    object List : Screen("list")
    object Profile : Screen("profile")

    object Detail : Screen("detail/{itemId}") {
        fun createRoute(itemId: Int): String = "detail/$itemId"
    }
}
