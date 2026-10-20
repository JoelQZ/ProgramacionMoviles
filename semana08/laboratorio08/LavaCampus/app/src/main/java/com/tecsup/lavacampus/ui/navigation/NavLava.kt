package com.tecsup.lavacampus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.estudiante.InicioScreen
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.LavaViewModelFactory
import com.tecsup.lavacampus.viewmodel.SesionViewModel

@Composable
fun NavLava(
    navController: NavHostController,
    sesionViewModel: SesionViewModel,
    repository: LavaRepository
) {
    val usuarioActual by sesionViewModel.usuarioActual.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = sesionViewModel,
                onLoginExitoso = { rol ->
                    if (rol == Rol.ADMIN.name) {
                        navController.navigate(Rutas.PANEL_ADMIN) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Rutas.INICIO_ESTUDIANTE) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Rutas.INICIO_ESTUDIANTE) {
            val usuario = usuarioActual
            if (usuario != null) {
                val inicioViewModel: InicioViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return InicioViewModel(repository, usuario) as T
                        }
                    }
                )

                InicioScreen(
                    usuario = usuario,
                    viewModel = inicioViewModel,
                    onNuevoPedido = { /* navController.navigate(Rutas.NUEVO_PEDIDO) */ },
                    onVerMisPedidos = { /* navController.navigate(Rutas.MIS_PEDIDOS) */ },
                    onVerTarifas = { /* navController.navigate(Rutas.TARIFAS) */ },
                    onVerDetallePedido = { pedidoId -> /* navController.navigate("detalle/$pedidoId") */ }
                )
            }
        }
    }
}