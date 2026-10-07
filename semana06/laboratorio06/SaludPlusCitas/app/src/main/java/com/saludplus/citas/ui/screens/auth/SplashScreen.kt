package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.theme.AzulFondoSuave
import com.saludplus.citas.ui.theme.AzulPrincipal

@Composable
fun SplashScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulFondoSuave)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Logo SaludPlus",
                tint = AzulPrincipal,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Clínica",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = AzulPrincipal
            )
            Text(
                text = "SaludPlus",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = AzulPrincipal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tu salud, nuestra prioridad",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate(Rutas.Registro.ruta)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
            ) {
                Text(text = "Comenzar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    navController.navigate(Rutas.Login.ruta)
                }
            ) {
                Text(text = "Ya tengo una cuenta", color = AzulPrincipal, fontWeight = FontWeight.Bold)
            }
        }
    }
}