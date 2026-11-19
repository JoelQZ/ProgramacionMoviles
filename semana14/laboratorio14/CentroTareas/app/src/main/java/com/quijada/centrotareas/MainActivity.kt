package com.quijada.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quijada.centrotareas.data.TareasRepositoryLocal
import com.quijada.centrotareas.ui.PantallaTareas
import com.quijada.centrotareas.ui.theme.CentroTareasTheme
import com.quijada.centrotareas.viewmodel.TareasViewModel
import com.quijada.centrotareas.viewmodel.TareasViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CentroTareasTheme {
                val factory = TareasViewModelFactory(TareasRepositoryLocal(applicationContext))
                val viewModel: TareasViewModel = viewModel(factory = factory)
                PantallaTareas(viewModel = viewModel)
            }
        }
    }
}
