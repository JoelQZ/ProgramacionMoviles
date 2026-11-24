package com.tecsup.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.murocampus.data.PostRepositoryRemote
import com.tecsup.murocampus.data.remote.RetrofitClient
import com.tecsup.murocampus.ui.PantallaCrear
import com.tecsup.murocampus.ui.PantallaDetalle
import com.tecsup.murocampus.ui.PantallaLista
import com.tecsup.murocampus.ui.theme.MuroCampusTheme
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.PostsViewModelFactory

sealed interface PantallaDestino {
    data object Lista : PantallaDestino
    data class Detalle(val id: Int) : PantallaDestino
    data object Crear : PantallaDestino
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuroCampusTheme {
                val repository = PostRepositoryRemote(RetrofitClient.api)
                val viewModel: PostsViewModel = viewModel(
                    factory = PostsViewModelFactory(repository)
                )

                var pantallaActual by remember { mutableStateOf<PantallaDestino>(PantallaDestino.Lista) }

                BackHandler(enabled = pantallaActual !is PantallaDestino.Lista) {
                    if (pantallaActual is PantallaDestino.Crear) {
                        viewModel.limpiarCreacion()
                    }
                    pantallaActual = PantallaDestino.Lista
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("MuroCampus") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    },
                    floatingActionButton = {
                        if (pantallaActual is PantallaDestino.Lista) {
                            FloatingActionButton(
                                onClick = {
                                    viewModel.limpiarCreacion()
                                    pantallaActual = PantallaDestino.Crear
                                },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Crear publicación")
                            }
                        }
                    }
                ) { innerPadding ->
                    when (val destino = pantallaActual) {
                        is PantallaDestino.Lista -> {
                            PantallaLista(
                                vm = viewModel,
                                onPostClick = { id ->
                                    pantallaActual = PantallaDestino.Detalle(id)
                                    viewModel.abrir(id)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is PantallaDestino.Detalle -> {
                            PantallaDetalle(
                                vm = viewModel,
                                postId = destino.id,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is PantallaDestino.Crear -> {
                            PantallaCrear(
                                vm = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
