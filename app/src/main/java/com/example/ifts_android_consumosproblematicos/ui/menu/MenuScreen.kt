package com.example.ifts_android_consumosproblematicos.ui.menu

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
import com.example.ifts_android_consumosproblematicos.ui.theme.*
import com.example.ifts_android_consumosproblematicos.util.SoundManager

enum class ModoSeleccionMenu {
    OLEADAS,
    ENDLESS,
    ARCADE
}

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
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    var modoSeleccionado by remember { mutableStateOf(ModoSeleccionMenu.OLEADAS) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoBase)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- 1. CABECERA Y SALUDO ---
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
                                fontSize = 53.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        ) {
                            append("¿Todo\n")
                        }
                        withStyle(
                            SpanStyle(
                                color = VioletaAccion,
                                fontSize = 53.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        ) {
                            append("Bien?")
                        }
                    },
                    textAlign = TextAlign.Center,
                    lineHeight = 54.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Simulador de situaciones sociales",
                    color = TextoBlanco.copy(alpha = 0.9f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                if (nombre.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                SoundManager.reproducirEfecto(context, R.raw.button1)
                                onEditarNombre()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.corazon_menu_hola, nombre),
                            color = TextoBlanco,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar nombre",
                            tint = CianAccion,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- 2. SELECCIÓN DE MODOS DE JUEGO ---
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 200)) +
                    scaleIn(initialScale = 0.95f, animationSpec = tween(800, delayMillis = 200)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fila de tarjetas de modos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TarjetitaModo(
                        titulo = "Oleadas",
                        seleccionado = modoSeleccionado == ModoSeleccionMenu.OLEADAS,
                        onClick = {
                            SoundManager.reproducirEfecto(context, R.raw.button1)
                            modoSeleccionado = ModoSeleccionMenu.OLEADAS
                        },
                        modifier = Modifier.weight(1f)
                    )
                    TarjetitaModo(
                        titulo = "Endless",
                        seleccionado = modoSeleccionado == ModoSeleccionMenu.ENDLESS,
                        onClick = {
                            SoundManager.reproducirEfecto(context, R.raw.button1)
                            modoSeleccionado = ModoSeleccionMenu.ENDLESS
                        },
                        modifier = Modifier.weight(1f)
                    )
                    TarjetitaModo(
                        titulo = "Minijuego\ncorazón",
                        seleccionado = modoSeleccionado == ModoSeleccionMenu.ARCADE,
                        onClick = {
                            SoundManager.reproducirEfecto(context, R.raw.button1)
                            modoSeleccionado = ModoSeleccionMenu.ARCADE
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción animada del modo seleccionado
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = TarjetaSituacion
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CianAccion.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = modoSeleccionado,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f)) togetherWith
                                        (fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 1.05f))
                            },
                            label = "descripcionModoTransition"
                        ) { modo ->
                            val descripcion = when (modo) {
                                ModoSeleccionMenu.OLEADAS -> "Completá 10 situaciones sociales equilibrando tu estabilidad emocional y la presión del entorno."
                                ModoSeleccionMenu.ENDLESS -> "Respondé situaciones infinitas con dificultad creciente y un temporizador cada vez más rápido."
                                ModoSeleccionMenu.ARCADE -> "Modo de acción: esquivá decisiones tóxicas, atrapá buenas vibras y desafiá el ranking global."
                            }
                            Text(
                                text = descripcion,
                                color = TextoBlanco,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- 3. BOTONES DE ACCIÓN ---
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 350)) +
                    slideInVertically(animationSpec = tween(800, delayMillis = 350, easing = FastOutSlowInEasing)) { it },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BotonMenu(
                    texto = stringResource(R.string.corazon_menu_iniciar),
                    onClick = {
                        when (modoSeleccionado) {
                            ModoSeleccionMenu.OLEADAS -> onIniciarPartida()
                            ModoSeleccionMenu.ENDLESS -> onEndless?.invoke()
                            ModoSeleccionMenu.ARCADE -> onArcade?.invoke()
                        }
                    }
                )

                BotonMenu(
                    texto = stringResource(R.string.corazon_menu_opciones),
                    onClick = onOpciones,
                    secundario = true
                )
            }
        }
    }
}

@Composable
private fun TarjetitaModo(
    titulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (seleccionado) VioletaAccion.copy(alpha = 0.85f) else TarjetaSituacion,
        animationSpec = tween(durationMillis = 200),
        label = "bgModoAnimation"
    )

    val borderColor by animateColorAsState(
        targetValue = if (seleccionado) CianAccion else Color.White.copy(alpha = 0.15f),
        animationSpec = tween(durationMillis = 200),
        label = "borderModoAnimation"
    )

    val scale by animateFloatAsState(
        targetValue = if (seleccionado) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scaleModoAnimation"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        border = BorderStroke(if (seleccionado) 2.5.dp else 1.dp, borderColor),
        modifier = modifier
            .height(88.dp)
            .scale(scale)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = titulo,
                color = if (seleccionado) TextoBlanco else TextoSecundario,
                fontSize = 15.sp,
                fontWeight = if (seleccionado) FontWeight.ExtraBold else FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun BotonMenu(
    texto: String,
    onClick: () -> Unit,
    secundario: Boolean = false
) {
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
            containerColor = if (secundario) RespuestaInactiva else CianAccion,
            contentColor = if (secundario) TextoBlanco else FondoBase
        ),
        border = if (secundario) BorderStroke(1.dp, VioletaAccion.copy(alpha = 0.8f)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
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
