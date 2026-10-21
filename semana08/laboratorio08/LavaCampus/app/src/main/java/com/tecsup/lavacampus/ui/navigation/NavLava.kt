package com.tecsup.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.tecsup.lavacampus.ui.administrador.AjustesScreen
import com.tecsup.lavacampus.ui.administrador.BandejaScreen
import com.tecsup.lavacampus.ui.administrador.ClientesScreen
import com.tecsup.lavacampus.ui.administrador.FormularioServicioScreen
import com.tecsup.lavacampus.ui.administrador.GestionPedidoScreen
import com.tecsup.lavacampus.ui.administrador.PanelScreen
import com.tecsup.lavacampus.ui.administrador.ServiciosScreen
import com.tecsup.lavacampus.ui.estudiante.DetallePedidoScreen
import com.tecsup.lavacampus.ui.estudiante.InicioScreen
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.MisPedidosScreen
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.ui.estudiante.PerfilScreen
import com.tecsup.lavacampus.ui.estudiante.SplashScreen
import com.tecsup.lavacampus.ui.estudiante.TarifasScreen
import com.tecsup.lavacampus.ui.theme.ColorEstudiante
import com.tecsup.lavacampus.viewmodel.BandejaViewModel
import com.tecsup.lavacampus.viewmodel.ClientesViewModel
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import com.tecsup.lavacampus.viewmodel.GestionPedidoViewModel
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.viewmodel.PanelViewModel
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
        startDestination = Rutas.SPLASH
    ) {
        composable(Rutas.SPLASH) {
            SplashScreen(
                onComenzar = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                    }
                }
            )
        }

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

        composable(Rutas.PANEL_ADMIN) {
            val panelViewModel: PanelViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return PanelViewModel(repository) as T
                    }
                }
            )

            PanelScreen(
                viewModel = panelViewModel,
                onVerPedidosListos = {
                    navController.navigate("bandeja_pedidos")
                },
                onNavegarTab = { ruta ->
                    if (ruta != Rutas.PANEL_ADMIN) {
                        navController.navigate(ruta) {
                            popUpTo(Rutas.PANEL_ADMIN)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(
            route = Rutas.BANDEJA_PEDIDOS,
            arguments = listOf(
                navArgument("usuarioId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: -1

            val bandejaViewModel: BandejaViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return BandejaViewModel(repository, usuarioId) as T
                    }
                }
            )

            BandejaScreen(
                viewModel = bandejaViewModel,
                onVerGestionPedido = { id ->
                    navController.navigate("gestion_pedido/$id")
                },
                onVolver = { navController.popBackStack() },
                onNavegarTab = { ruta ->
                    if (ruta != Rutas.BANDEJA_PEDIDOS) {
                        navController.navigate(ruta) {
                            popUpTo(Rutas.PANEL_ADMIN)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(
            route = Rutas.GESTION_PEDIDO,
            arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getInt("pedidoId") ?: return@composable

            val gestionViewModel: GestionPedidoViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return GestionPedidoViewModel(repository, pedidoId) as T
                    }
                }
            )

            GestionPedidoScreen(
                viewModel = gestionViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.SERVICIOS_ADMIN) {
            val serviciosViewModel: ServiciosViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return ServiciosViewModel(repository) as T
                    }
                }
            )

            ServiciosScreen(
                viewModel = serviciosViewModel,
                onNuevoServicio = {
                    navController.navigate("formulario_servicio")
                },
                onEditarServicio = { servicio ->
                    navController.navigate("formulario_servicio?servicioId=${servicio.id}")
                },
                onNavegarTab = { ruta ->
                    if (ruta != Rutas.SERVICIOS_ADMIN) {
                        navController.navigate(ruta) {
                            popUpTo(Rutas.PANEL_ADMIN)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(
            route = Rutas.FORMULARIO_SERVICIO,
            arguments = listOf(
                navArgument("servicioId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val servicioId = backStackEntry.arguments?.getInt("servicioId") ?: -1

            val serviciosViewModel: ServiciosViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return ServiciosViewModel(repository) as T
                    }
                }
            )

            val todosServicios by serviciosViewModel.todosLosServicios.collectAsState()
            val servicioAEditar = remember(todosServicios, servicioId) {
                todosServicios.find { it.id == servicioId }
            }

            FormularioServicioScreen(
                servicioAEditar = servicioAEditar,
                onVolver = { navController.popBackStack() },
                onGuardar = { id, nombre, desc, precio, unidad, activo ->
                    serviciosViewModel.guardarNuevoOSistemaServicio(
                        id = id,
                        nombre = nombre,
                        descripcion = desc,
                        precio = precio,
                        unidad = unidad,
                        activo = activo
                    )
                }
            )
        }

        composable(Rutas.CLIENTES_ADMIN) {
            val clientesViewModel: ClientesViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return ClientesViewModel(repository) as T
                    }
                }
            )

            ClientesScreen(
                viewModel = clientesViewModel,
                onVerHistorialCliente = { usuarioId ->
                    navController.navigate("bandeja_pedidos?usuarioId=$usuarioId")
                },
                onNavegarTab = { ruta ->
                    if (ruta != Rutas.CLIENTES_ADMIN) {
                        navController.navigate(ruta) {
                            popUpTo(Rutas.PANEL_ADMIN)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(Rutas.AJUSTES_ADMIN) {
            AjustesScreen(
                onCambiarPerfil = {
                    sesionViewModel.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onCerrarSesion = {
                    sesionViewModel.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavegarTab = { ruta ->
                    if (ruta != Rutas.AJUSTES_ADMIN) {
                        navController.navigate(ruta) {
                            popUpTo(Rutas.PANEL_ADMIN)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(Rutas.PERFIL_ESTUDIANTE) {
            val usuario = usuarioActual
            if (usuario != null) {
                PerfilScreen(
                    usuario = usuario,
                    repository = repository,
                    onCambiarPerfil = {
                        sesionViewModel.cerrarSesion()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onCerrarSesion = {
                        sesionViewModel.cerrarSesion()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavegarTab = { ruta ->
                        if (ruta != Rutas.PERFIL_ESTUDIANTE) {
                            navController.navigate(ruta) {
                                popUpTo(Rutas.INICIO_ESTUDIANTE)
                                launchSingleTop = true
                            }
                        }
                    }
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