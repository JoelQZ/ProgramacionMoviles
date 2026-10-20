package com.tecsup.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.tecsup.lavacampus.ui.estudiante.MisPedidosScreen
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.ui.estudiante.TarifasScreen
import com.tecsup.lavacampus.ui.theme.ColorEstudiante
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel
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
                    onNuevoPedido = { navController.navigate(Rutas.NUEVO_PEDIDO) },
                    onVerMisPedidos = { navController.navigate(Rutas.MIS_PEDIDOS) },
                    onVerTarifas = { navController.navigate(Rutas.TARIFAS) },
                    onVerDetallePedido = { pedidoId ->
                        navController.navigate("detalle_pedido/$pedidoId")
                    },
                    onNavegarTab = { ruta ->
                        if (ruta != Rutas.INICIO_ESTUDIANTE) {
                            navController.navigate(ruta) {
                                popUpTo(Rutas.INICIO_ESTUDIANTE)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorEstudiante)
                }
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

        composable(Rutas.MIS_PEDIDOS) {
            val usuario = usuarioActual
            if (usuario != null) {
                val misPedidosViewModel: MisPedidosViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return MisPedidosViewModel(repository, usuario) as T
                        }
                    }
                )

                MisPedidosScreen(
                    viewModel = misPedidosViewModel,
                    onNuevoPedido = { navController.navigate(Rutas.NUEVO_PEDIDO) },
                    onVerDetallePedido = { pedidoId ->
                        navController.navigate("detalle_pedido/$pedidoId")
                    },
                    onVolver = { navController.popBackStack() },
                    onNavegarTab = { ruta ->
                        if (ruta != Rutas.MIS_PEDIDOS) {
                            navController.navigate(ruta) {
                                popUpTo(Rutas.INICIO_ESTUDIANTE)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }

        composable(Rutas.TARIFAS) {
            val serviciosViewModel: ServiciosViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return ServiciosViewModel(repository) as T
                    }
                }
            )

            TarifasScreen(
                viewModel = serviciosViewModel,
                onVolver = { navController.popBackStack() },
                onNavegarTab = { ruta ->
                    if (ruta != Rutas.TARIFAS) {
                        navController.navigate(ruta) {
                            popUpTo(Rutas.INICIO_ESTUDIANTE)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}