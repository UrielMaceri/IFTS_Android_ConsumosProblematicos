package com.example.ifts_android_consumosproblematicos.ui.menu

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ifts_android_consumosproblematicos.R
import com.example.ifts_android_consumosproblematicos.ui.theme.CianAccion
import com.example.ifts_android_consumosproblematicos.ui.theme.FondoBase
import com.example.ifts_android_consumosproblematicos.ui.theme.TextoBlanco
import com.example.ifts_android_consumosproblematicos.ui.theme.TodoBienTheme
import com.example.ifts_android_consumosproblematicos.ui.theme.VioletaAccion
import com.example.ifts_android_consumosproblematicos.util.SoundManager

@Composable
fun MenuScreen(
    onIniciarPartida: () -> Unit,
    onOpciones: () -> Unit,
    modifier: Modifier = Modifier,
    onEndless: (() -> Unit)? = null
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoBase)
            .systemBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título y Subtítulo animados
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(700)) +
                    slideInVertically(animationSpec = tween(700, easing = FastOutSlowInEasing)) { -it }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = CianAccion,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        ) {
                            append("¿Todo\n")
                        }
                        withStyle(
                            SpanStyle(
                                color = VioletaAccion,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        ) {
                            append("Bien?")
                        }
                    },
                    textAlign = TextAlign.Center,
                    lineHeight = 68.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Simulador de situaciones sociales",
                    color = TextoBlanco.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(56.dp))

        // Botón animado
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 300)) +
                    slideInVertically(animationSpec = tween(800, delayMillis = 300, easing = FastOutSlowInEasing)) { it },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BotonMenu("Iniciar Partida", onIniciarPartida)
                // Botón Opciones oculto por el momento para el usuario
                /*
                Spacer(Modifier.height(16.dp))
                BotonMenu("Opciones", onOpciones)
                */
            }
        }
    }
}

@Composable
private fun BotonMenu(texto: String, onClick: () -> Unit) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "menuButtonScale"
    )

    Button(
        onClick = {
            SoundManager.reproducirEfecto(context, R.raw.button1)
            onClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CianAccion,
            contentColor = FondoBase
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuScreenPreview() {
    TodoBienTheme { MenuScreen(onIniciarPartida = {}, onOpciones = {}) }
}
