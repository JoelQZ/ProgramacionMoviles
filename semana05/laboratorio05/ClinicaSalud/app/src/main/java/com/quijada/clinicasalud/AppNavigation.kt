package com.quijada.clinicasalud

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DoctorDetail : Screen("doctor_detail/{medicoId}") {
        fun createRoute(medicoId: Int) = "doctor_detail/$medicoId"
    }
    object Schedule : Screen("schedule/{medicoId}") {
        fun createRoute(medicoId: Int) = "schedule/$medicoId"
    }
    object Confirmation : Screen("confirmation")
    object Appointments : Screen("appointments")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onDoctorSelect = { medicoId ->
                    navController.navigate(Screen.DoctorDetail.createRoute(medicoId))
                }
            )
        }

        composable(
            route = Screen.DoctorDetail.route,
            arguments = listOf(navArgument("medicoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 1
            DoctorDetailScreen(
                medicoId = medicoId,
                onAgendarClick = {
                    navController.navigate(Screen.Schedule.createRoute(medicoId))
                }
            )
        }

        composable(
            route = Screen.Schedule.route,
            arguments = listOf(navArgument("medicoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 1
            ScheduleScreen(
                medicoId = medicoId,
                onConfirmarClick = {
                    navController.navigate(Screen.Confirmation.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.Confirmation.route) {
            ConfirmationScreen(
                onVolverInicioClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Appointments.route) {
            AppointmentsScreen()
        }
    }
}