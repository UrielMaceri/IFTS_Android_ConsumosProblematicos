package com.example.ifts_android_consumosproblematicos.ui.corazon

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ifts_android_consumosproblematicos.R
import com.example.ifts_android_consumosproblematicos.data.Repos
import com.example.ifts_android_consumosproblematicos.ui.theme.CianAccion
import com.example.ifts_android_consumosproblematicos.ui.theme.FondoBase
import com.example.ifts_android_consumosproblematicos.ui.theme.TextoBlanco
import kotlinx.coroutines.launch

private val PALETA_COLORES = listOf(
    Color(0xFFFF4D79), // Rosa / Magenta original
    Color(0xFFE74C3C), // Rojo
    Color(0xFFFF922B), // Naranja
    Color(0xFFFFD43B), // Amarillo
    Color(0xFF51CF66), // Verde
    Color(0xFF339AF0), // Azul
    Color(0xFF845EF7), // Violeta
    Color(0xFFF0659E)  // Rosa claro
)

@Composable
fun ArcadeSetupScreen(
    onIniciarArcade: () -> Unit = {},
    onVerRanking: () -> Unit = {},
    onVolver: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val colorGuardadoArgb by Repos.prefs.colorCorazon.collectAsState(initial = 0xFFFF4D79.toInt())

    var colorElegido by remember { mutableStateOf(Color(colorGuardadoArgb)) }
    var hue by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(colorGuardadoArgb) {
        colorElegido = Color(colorGuardadoArgb)
    }

    BackHandler {
        onVolver()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoBase)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.corazon_arcade_setup_titulo),
            color = CianAccion,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Vista previa del corazón
        Box(
            modifier = Modifier
                .size(160.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val s = size.minDimension * 0.75f
                val heartPath = Path().apply {
                    moveTo(0f, s * 0.35f)
                    cubicTo(-s * 0.6f, -s * 0.15f, -s * 0.6f, -s * 0.75f, 0f, -s * 0.35f)
                    cubicTo(s * 0.6f, -s * 0.75f, s * 0.6f, -s * 0.15f, 0f, s * 0.35f)
                    close()
                }
                withTransform({ translate(left = size.width / 2f, top = size.height / 2f) }) {
                    drawPath(path = heartPath, color = colorElegido, style = Fill)
                }
            }
        }

        // Selector de color (Paleta + Slider de matiz)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Paleta de 8 colores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PALETA_COLORES.forEach { c ->
                    val esSeleccionado = c == colorElegido
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(
                                width = if (esSeleccionado) 3.dp else 0.dp,
                                color = if (esSeleccionado) TextoBlanco else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                colorElegido = c
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Control de matiz (Slider de 0 a 360)
            Text(
                text = stringResource(R.string.corazon_arcade_setup_matiz),
                color = TextoBlanco.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Slider(
                value = hue,
                onValueChange = { h ->
                    hue = h
                    colorElegido = Color.hsv(h, 0.8f, 1f)
                },
                valueRange = 0f..360f,
                colors = SliderDefaults.colors(
                    thumbColor = colorElegido,
                    activeTrackColor = colorElegido,
                    inactiveTrackColor = TextoBlanco.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Botones de acción
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    scope.launch {
                        Repos.prefs.setColorCorazon(colorElegido.toArgb())
                        onIniciarArcade()
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CianAccion,
                    contentColor = FondoBase
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = stringResource(R.string.corazon_arcade_btn_jugar),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onVerRanking,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoBlanco),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = stringResource(R.string.corazon_arcade_btn_ranking),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
