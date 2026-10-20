package com.tecsup.lavacampus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.estudiante.DetallePedidoScreen
import com.tecsup.lavacampus.ui.estudiante.InicioScreen
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.LavaViewModelFactory
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
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
                    onNuevoPedido = {
                        navController.navigate(Rutas.NUEVO_PEDIDO)
                    },
                    onVerMisPedidos = { /* navController.navigate(...) */ },
                    onVerTarifas = { /* navController.navigate(...) */ },
                    onVerDetallePedido = { pedidoId -> /* navController.navigate(...) */ }
                )
            }
        }
        composable(Rutas.NUEVO_PEDIDO) {
            val usuario = usuarioActual
            if (usuario != null) {
                val nuevoPedidoViewModel: NuevoPedidoViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return NuevoPedidoViewModel(repository, usuario) as T
                        }
                    }
                )

                NuevoPedidoScreen(
                    viewModel = nuevoPedidoViewModel,
                    onVolver = { navController.popBackStack() },
                    onPedidoConfirmado = {
                        navController.navigate(Rutas.INICIO_ESTUDIANTE) {
                            popUpTo(Rutas.INICIO_ESTUDIANTE) { inclusive = true }
                        }
                    }
                )
            }
        }
        composable(
            route = Rutas.DETALLE_PEDIDO,
            arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getInt("pedidoId") ?: return@composable

            val detalleViewModel: DetallePedidoViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return DetallePedidoViewModel(repository, pedidoId) as T
                    }
                }
            )

            DetallePedidoScreen(
                viewModel = detalleViewModel,
                onVolver = { navController.popBackStack() }
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
                    onNuevoPedido = { navController.navigate(Rutas.NUEVO_PEDIDO) },
                    onVerMisPedidos = { /* navController.navigate(Rutas.MIS_PEDIDOS) */ },
                    onVerTarifas = { /* navController.navigate(Rutas.TARIFAS) */ },
                    onVerDetallePedido = { pedidoId ->
                        navController.navigate("detalle_pedido/$pedidoId") // <-- Navega al detalle pasando el ID
                    }
                )
            }
        }
    }
}