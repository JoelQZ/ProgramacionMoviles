package com.tecsup.lavacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.data.repository.LavaRepositoryRoom
import com.tecsup.lavacampus.ui.navigation.NavLava
import com.tecsup.lavacampus.ui.theme.LavaCampusTheme
import com.tecsup.lavacampus.viewmodel.LavaViewModelFactory
import com.tecsup.lavacampus.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as LavaApp
        val repository = LavaRepositoryRoom(app.database)
        val prefs = PreferenciasStore(this)
        val factory = LavaViewModelFactory(repository, prefs)

        setContent {
            LavaCampusTheme {
                MainContent(factory = factory, repository = repository)
            }
        }
    }
}

@Composable
private fun MainContent(factory: LavaViewModelFactory, repository: LavaRepository) {
    val navController = rememberNavController()
    val sesionViewModel: SesionViewModel = viewModel(factory = factory)

    NavLava(
        navController = navController,
        sesionViewModel = sesionViewModel,
        repository = repository
    )
}