package com.example.ifts_android_consumosproblematicos.ui.finalScreens

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.ifts_android_consumosproblematicos.R
import com.example.ifts_android_consumosproblematicos.util.SoundManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ifts_android_consumosproblematicos.ui.theme.FondoBase
import com.example.ifts_android_consumosproblematicos.ui.theme.RojoGameOver
import com.example.ifts_android_consumosproblematicos.ui.theme.TextoSecundario

import androidx.compose.ui.tooling.preview.Preview
import com.example.ifts_android_consumosproblematicos.ui.theme.CianAccion
import com.example.ifts_android_consumosproblematicos.ui.theme.TarjetaSituacion
import com.example.ifts_android_consumosproblematicos.ui.theme.TextoBlanco
import com.example.ifts_android_consumosproblematicos.ui.theme.TodoBienTheme

@Composable
fun GameOverScreen(
    motivo: String = "Tus niveles de estabilidad o presión alcanzaron el punto límite.",
    onReintentar: () -> Unit,
    onVolverMenu: () -> Unit
) {
    val context = LocalContext.current

    // Hace sonar el efecto de derrota automáticamente al entrar
    LaunchedEffect(Unit) {
        SoundManager.reproducirEfecto(context, R.raw.gameover1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoBase)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "GAME OVER",
            color = RojoGameOver,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "¿Querés volver a intentarlo?",
            color = TextoSecundario,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Card con Motivo de Derrota

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = TarjetaSituacion)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Reflexión",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = motivo,
                    color = TextoBlanco,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onReintentar,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CianAccion,
                contentColor = FondoBase
            )
        ) {
            Text(
                text = "REINTENTAR",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onVolverMenu,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoBlanco)
        ) {
            Text(
                text = "VOLVER AL MENÚ",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverScreenPreview() {
    TodoBienTheme {
        GameOverScreen(
            motivo = "Tus niveles de estabilidad o presión alcanzaron el punto límite.",
            onReintentar = {},
            onVolverMenu = {}
        )
    }
}