package com.quijada.campusgps

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.quijada.campusgps.data.LugaresRepositoryLocal
import com.quijada.campusgps.data.PreferenciasRepositoryDataStore
import com.quijada.campusgps.data.UbicacionRepositoryGoogle
import com.quijada.campusgps.ui.PantallaMapa
import com.quijada.campusgps.ui.theme.CampusGPSTheme
import com.quijada.campusgps.viewmodel.CampusViewModelFactory
import com.quijada.campusgps.viewmodel.MapaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MapaViewModel by viewModels {
        CampusViewModelFactory(
            ubicacionRepo = UbicacionRepositoryGoogle(applicationContext),
            lugaresRepo = LugaresRepositoryLocal(),
            preferenciasRepo = PreferenciasRepositoryDataStore(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            CampusGPSTheme {
                PantallaMapa(vm = viewModel)
            }
        }
    }
}
