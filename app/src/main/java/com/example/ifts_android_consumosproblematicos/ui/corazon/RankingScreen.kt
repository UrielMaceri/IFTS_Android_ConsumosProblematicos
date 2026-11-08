package com.example.ifts_android_consumosproblematicos.ui.corazon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.ifts_android_consumosproblematicos.data.ranking.EntradaRanking
import com.example.ifts_android_consumosproblematicos.ui.theme.*
import kotlinx.coroutines.launch




@Composable
fun RankingScreen(
    onVolver: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }
    var listaRanking by remember { mutableStateOf<List<EntradaRanking>>(emptyList()) }
    var miUid by remember { mutableStateOf<String?>(null) }

    fun cargarRanking() {
        scope.launch {
            cargando = true
            error = false
            try {
                miUid = Repos.ranking.miUid()
                val resultado = Repos.ranking.top(20)
                if (resultado.isSuccess) {
                    listaRanking = resultado.getOrNull() ?: emptyList()
                    cargando = false
                } else {
                    error = true
                    cargando = false
                }
            } catch (_: Exception) {
                error = true
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarRanking()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoBase)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "RANKING GLOBAL",
            color = CianAccion,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            cargando -> {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CianAccion)
                }
            }
            error -> {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "No se pudo cargar el ranking", color = RojoGameOver, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { cargarRanking() }, colors = ButtonDefaults.buttonColors(containerColor = CianAccion, contentColor = FondoBase)) {
                            Text(text = "Reintentar")
                        }
                    }
                }
            }
            listaRanking.isEmpty() -> {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No hay registros en el ranking todavía", color = TextoSecundario, fontSize = 16.sp)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(listaRanking) { index, entrada ->
                        val esMio = entrada.uid == miUid
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (esMio) TarjetaSituacion else TarjetaSituacion.copy(alpha = 0.6f)
                            ),
                            border = if (esMio) androidx.compose.foundation.BorderStroke(1.5.dp, CianAccion) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text(
                                        text = "${index + 1}°",
                                        color = if (index == 0) AmarilloAlerta else TextoSecundario,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(32.dp)
                                    )
                                    Text(
                                        text = entrada.nombre,
                                        color = if (esMio) CianAccion else TextoBlanco,
                                        fontSize = 16.sp,
                                        fontWeight = if (esMio) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${entrada.puntaje} pts",
                                        color = TextoBlanco,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Combo max: ${entrada.comboMax}",
                                        color = TextoSecundario,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoBlanco)
        ) {
            Text(text = "VOLVER", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
