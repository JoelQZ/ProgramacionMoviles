package com.saludplus.citas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saludplus.citas.ui.screens.agendamiento.*
import com.saludplus.citas.ui.screens.auth.LoginScreen
import com.saludplus.citas.ui.screens.auth.RegistroScreen
import com.saludplus.citas.ui.screens.auth.SplashScreen
import com.saludplus.citas.ui.screens.auth.TerminosScreen
import com.saludplus.citas.ui.screens.citas.*
import com.saludplus.citas.ui.screens.home.*
import com.saludplus.citas.ui.screens.notificaciones.*
import com.saludplus.citas.ui.screens.perfil.*
import com.saludplus.citas.ui.screens.resultados.*

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Rutas.Splash.ruta
    ) {
        composable(Rutas.Splash.ruta) { SplashScreen(navController) }
        composable(Rutas.Login.ruta) { LoginScreen(navController) }
        composable(Rutas.Registro.ruta) { RegistroScreen(navController) }
        composable(Rutas.Terminos.ruta) { TerminosScreen(navController) }

        composable(Rutas.Home.ruta) { HomeScreen(navController) }

        composable(Rutas.Especialidades.ruta) { EspecialidadesScreen(navController) }
        composable(Rutas.Medicos.ruta) { backStackEntry ->
            val especialidadId = backStackEntry.arguments?.getString("especialidadId") ?: ""
            MedicosScreen(navController, especialidadId)
        }
        composable(Rutas.FechaHora.ruta) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getString("medicoId") ?: ""
            FechaHoraScreen(navController, medicoId)
        }
        composable(Rutas.ConfirmarCita.ruta) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getString("medicoId") ?: ""
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val hora = backStackEntry.arguments?.getString("hora") ?: ""
            ConfirmarCitaScreen(navController, medicoId, fecha, hora)
        }
        composable(Rutas.CitaExitosa.ruta) { CitaExitosaScreen(navController) }

        composable(Rutas.MisCitas.ruta) { MisCitasScreen(navController) }
        composable(Rutas.DetalleCita.ruta) { backStackEntry ->
            val citaId = backStackEntry.arguments?.getString("citaId") ?: ""
            DetalleCitaScreen(navController, citaId)
        }
        composable(Rutas.Perfil.ruta) { PerfilScreen(navController) }
        composable(Rutas.Resultados.ruta) { ResultadosScreen(navController) }
        composable(Rutas.Notificaciones.ruta) { NotificacionesScreen(navController) }
    }
}