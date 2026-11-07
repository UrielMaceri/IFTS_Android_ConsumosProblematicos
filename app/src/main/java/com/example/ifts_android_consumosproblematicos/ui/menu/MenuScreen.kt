package com.example.ifts_android_consumosproblematicos.ui.menu

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
    onEndless: (() -> Unit)? = null,
    nombre: String = "",
    onArcade: (() -> Unit)? = null,
    onEditarNombre: () -> Unit = {}
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

                if (nombre.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.clickable { onEditarNombre() }
                    ) {
                        Text(
                            text = stringResource(R.string.corazon_menu_hola, nombre),
                            color = TextoBlanco,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar nombre",
                            tint = CianAccion,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        // Botones animados
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
                BotonMenu(stringResource(R.string.corazon_menu_iniciar), onIniciarPartida)
                Spacer(Modifier.height(12.dp))
                BotonMenu(stringResource(R.string.corazon_menu_endless)) { onEndless?.invoke() }
                Spacer(Modifier.height(12.dp))
                BotonMenu(stringResource(R.string.corazon_menu_modo_corazon)) { onArcade?.invoke() }
                Spacer(Modifier.height(12.dp))
                BotonMenu(stringResource(R.string.corazon_menu_opciones), onOpciones)
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
    TodoBienTheme { MenuScreen(onIniciarPartida = {}, onOpciones = {}, nombre = "Jugador") }
}
