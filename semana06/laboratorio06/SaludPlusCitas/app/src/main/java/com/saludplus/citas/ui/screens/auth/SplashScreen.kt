package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saludplus.citas.R
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.theme.AzulFondoSuave
import com.saludplus.citas.ui.theme.AzulPrincipal

@Composable
fun SplashScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AzulFondoSuave
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Logo SaludPlus",
                    tint = AzulPrincipal,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Clínica",
                    fontSize = 18.sp,
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

            Image(
                painter = painterResource(id = R.drawable.img_doctor),
                contentDescription = "Doctor SaludPlus",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
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

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        navController.navigate(Rutas.Login.ruta)
                    }
                ) {
                    Text(
                        text = "Ya tengo una cuenta",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}