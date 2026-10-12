package com.example.ifts_android_consumosproblematicos.ui.finalScreens

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.ifts_android_consumosproblematicos.R
import com.example.ifts_android_consumosproblematicos.util.SoundManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ifts_android_consumosproblematicos.ui.theme.CianAccion
import com.example.ifts_android_consumosproblematicos.ui.theme.EstabilidadEmocional
import com.example.ifts_android_consumosproblematicos.ui.theme.FondoBase
import com.example.ifts_android_consumosproblematicos.ui.theme.PresionSocial
import com.example.ifts_android_consumosproblematicos.ui.theme.TarjetaSituacion
import com.example.ifts_android_consumosproblematicos.ui.theme.TextoBlanco
import com.example.ifts_android_consumosproblematicos.ui.theme.TextoSecundario
import com.example.ifts_android_consumosproblematicos.ui.theme.TodoBienTheme

@Composable
fun SuccessScreen(
    estabilidadFinal: Int = 100,
    presionFinal: Int = 50,
    onVolverMenu: () -> Unit
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        SoundManager.reproducirEfecto(context, R.raw.success, volumen = 0.3f)
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
            text = "¡VICTORIA!",
            color = EstabilidadEmocional,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "¡Completaste todas las rondas con éxito!",
            color = TextoSecundario,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TarjetaSituacion)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Estadísticas Finales",
                    color = TextoBlanco,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Estabilidad",
                            color = TextoSecundario,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$estabilidadFinal%",
                            color = EstabilidadEmocional,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Presión",
                            color = TextoSecundario,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$presionFinal%",
                            color = PresionSocial,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onVolverMenu,
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
                text = "MENÚ",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuccessScreenPreview() {
    TodoBienTheme {
        SuccessScreen(
            estabilidadFinal = 88,
            presionFinal = 12,
            onVolverMenu = {}
        )
    }
}
