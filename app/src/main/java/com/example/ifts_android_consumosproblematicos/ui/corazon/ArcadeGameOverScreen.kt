package com.example.ifts_android_consumosproblematicos.ui.corazon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ifts_android_consumosproblematicos.data.Repos
import com.example.ifts_android_consumosproblematicos.ui.theme.*
import kotlinx.coroutines.launch






@Composable
fun ArcadeGameOverScreen(
    puntaje: Int = 0,
    comboMax: Int = 0,
    segundos: Int = 0,
    onReintentar: () -> Unit = {},
    onVerRanking: () -> Unit = {},
    onVolverMenu: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var estadoEnvio by remember { mutableStateOf("Enviando…") }
    var enviadoExitoso by remember { mutableStateOf(false) }

    fun enviarPuntaje() {
        scope.launch {
            estadoEnvio = "Enviando…"
            val nombre = Repos.jugador.obtenerNombre() ?: "Anónimo"
            Repos.prefs.registrarArcade(puntaje, comboMax)
            val resultado = Repos.ranking.enviar(nombre, puntaje, comboMax)
            if (resultado.isSuccess) {
                estadoEnvio = "Enviado"
                enviadoExitoso = true
            } else {
                estadoEnvio = "No se pudo enviar"
                enviadoExitoso = false
            }
        }
    }

    LaunchedEffect(Unit) {
        enviarPuntaje()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoBase)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PARTIDA TERMINADA",
            color = RojoGameOver,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

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
                    text = "Tus Estadísticas",
                    color = TextoSecundario,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Puntaje:", color = TextoBlanco, fontSize = 16.sp)
                    Text(text = "$puntaje", color = CianAccion, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Combo Máximo:", color = TextoBlanco, fontSize = 16.sp)
                    Text(text = "$comboMax", color = AmarilloAlerta, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Tiempo:", color = TextoBlanco, fontSize = 16.sp)
                    Text(text = "${segundos}s", color = TextoBlanco, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Ranking: ", color = TextoSecundario, fontSize = 14.sp)
            Text(text = estadoEnvio, color = if (enviadoExitoso) CorazonVerdeBueno else AmarilloAlerta, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            if (!enviadoExitoso && estadoEnvio == "No se pudo enviar") {
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { enviarPuntaje() }) {
                    Text(text = "Reintentar", color = CianAccion)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onReintentar,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CianAccion, contentColor = FondoBase)
        ) {
            Text(text = "JUGAR DE NUEVO", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onVerRanking,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoBlanco)
        ) {
            Text(text = "VER RANKING", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onVolverMenu) {
            Text(text = "Volver al Menú", color = TextoSecundario, fontSize = 15.sp)
        }
    }
}
