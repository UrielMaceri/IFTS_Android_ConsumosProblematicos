package com.example.ifts_android_consumosproblematicos.ui.corazon

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ifts_android_consumosproblematicos.model.EstadoHud
import com.example.ifts_android_consumosproblematicos.ui.theme.*

@Composable
fun HudArcade(hud: EstadoHud, modifier: Modifier = Modifier) {
    val estabilidadAnimada by animateFloatAsState(
        targetValue = hud.estabilidad / 100f,
        label = "estabilidadAnimada"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ESTABILIDAD",
                color = TextoSecundario,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${hud.estabilidad}%",
                color = EstabilidadEmocional,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { estabilidadAnimada },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = EstabilidadEmocional,
            trackColor = FondoInactivoBarras
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ESCUDO EMOCIONAL",
                    color = TextoSecundario,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 0 until 5) {
                        val activo = hud.escudo || i < hud.progresoEscudo
                        Box(
                            modifier = Modifier
                                .size(width = 18.dp, height = 8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (activo) CorazonEscudoColor else FondoInactivoBarras)
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "PUNTAJE",
                        color = TextoSecundario,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${hud.puntaje}",
                        color = TextoBlanco,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "COMBO ${hud.combo}",
                        color = AmarilloAlerta,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "×${hud.multiplicador}",
                        color = CianAccion,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun HudRecuperacion(estabilidad: Int, modifier: Modifier = Modifier) {
    val estabilidadAnimada by animateFloatAsState(
        targetValue = estabilidad / 100f,
        label = "estabilidadRecupAnim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BUENAS VIBRAS",
                color = TextoSecundario,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$estabilidad/100",
                color = CorazonVerdeBueno,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { estabilidadAnimada },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = CorazonVerdeBueno,
            trackColor = FondoInactivoBarras
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F1A)
@Composable
fun HudArcadePreview() {
    TodoBienTheme {
        HudArcade(
            hud = EstadoHud(
                estabilidad = 75,
                combo = 8,
                puntaje = 340,
                multiplicador = 2,
                escudo = false,
                progresoEscudo = 3
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F1A)
@Composable
fun HudRecuperacionPreview() {
    TodoBienTheme {
        HudRecuperacion(estabilidad = 50)
    }
}
