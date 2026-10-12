package com.example.ifts_android_consumosproblematicos.ui.juego

import android.graphics.BitmapFactory
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ifts_android_consumosproblematicos.R
import com.example.ifts_android_consumosproblematicos.model.Efecto
import com.example.ifts_android_consumosproblematicos.model.ModoJuego
import com.example.ifts_android_consumosproblematicos.model.Opcion
import com.example.ifts_android_consumosproblematicos.model.TipoOpcion
import com.example.ifts_android_consumosproblematicos.ui.theme.*

@Composable
fun JuegoScreen(
    modo: ModoJuego,
    estabilidad: Int,
    presion: Int,
    rondaActual: Int,
    totalRondas: Int,
    textoSituacion: String,
    imagenNombre: String,
    opciones: List<Opcion>,
    tiempoRestanteSeg: Int,
    onOpcionSelected: (Opcion) -> Unit,
    onSalir: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val imageBitmap = remember(imagenNombre) {
        if (imagenNombre.isNotBlank()) {
            try {
                context.assets.open(imagenNombre).use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                }
            } catch (_: Exception) {
                null
            }
        } else null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoBase)
            .systemBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- 1. HUD SUPERIOR ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BarraHud(
                        label = "ESTABILIDAD",
                        valor = estabilidad,
                        colorBarra = EstabilidadEmocional
                    )
                    BarraHud(
                        label = "PRESIÓN SOCIAL",
                        valor = presion,
                        colorBarra = PresionSocial
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    color = TarjetaSituacion,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(2.dp, AmarilloAlerta.copy(alpha = 0.8f)),
                    modifier = Modifier.width(76.dp)
                ) {
                    AnimatedContent(
                        targetState = rondaActual,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(250)) + scaleIn(initialScale = 0.8f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))) togetherWith
                                (fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 1.2f, animationSpec = tween(150)))
                        },
                        label = "rondaBadgeAesthetic"
                    ) { ronda ->
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "RONDA",
                                color = TextoSecundario,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ronda.toString(),
                                color = TextoBlanco,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onSalir,
                    modifier = Modifier
                        .size(40.dp)
                        .background(TarjetaSituacion, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Salir al menú",
                        tint = TextoBlanco
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 2. CONTENEDOR VISUAL CON ZOOM/FADE ESTÉTICO (SIN DESLIZAR HACIA ABAJO) ---
        AnimatedContent(
            targetState = imagenNombre,
            transitionSpec = {
                (fadeIn(animationSpec = tween(350)) + scaleIn(initialScale = 0.96f, animationSpec = tween(350))) togetherWith
                    (fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 1.03f, animationSpec = tween(200)))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
            label = "imagenTransitionAesthetic"
        ) { imgPath ->
            Card(
                colors = CardDefaults.cardColors(containerColor = TarjetaSituacion),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            ) {
                if (imgPath.isNotBlank() && imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Ilustración de la situación actual",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.s1),
                        contentDescription = "Ilustración de la situación actual",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 3. TARJETA DE SITUACIÓN Y OPCIONES CON ZOOM/FADE LIMPIO (SIN DESLIZAR ABAJO) ---
        AnimatedContent(
            targetState = textoSituacion,
            transitionSpec = {
                (fadeIn(animationSpec = tween(350)) + scaleIn(initialScale = 0.97f, animationSpec = tween(350))) togetherWith
                    (fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 1.02f, animationSpec = tween(200)))
            },
            modifier = Modifier.fillMaxWidth(),
            label = "situacionTransitionAesthetic"
        ) { currentTexto ->
            Card(
                colors = CardDefaults.cardColors(containerColor = TarjetaSituacion),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(VioletaAccion.copy(alpha = 0.35f), RoundedCornerShape(50.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(EstabilidadEmocional, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SITUACIÓN",
                                color = InsigniaEducativo,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentTexto,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextoBlanco,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Opciones con aparición limpia basada en Fade + Scale (sin deslizar abajo)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        opciones.forEachIndexed { index, opcion ->
                            var itemVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(currentTexto) {
                                itemVisible = false
                                kotlinx.coroutines.delay((index * 50).toLong())
                                itemVisible = true
                            }

                            AnimatedVisibility(
                                visible = itemVisible,
                                enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                                exit = fadeOut(animationSpec = tween(150)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                BotonRespuestaEstiloP2(
                                    numero = index + 1,
                                    texto = opcion.texto,
                                    onClick = { onOpcionSelected(opcion) }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 4. FOOTER / TEMPORIZADOR ESTÉTICO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val timerColor by animateColorAsState(
                targetValue = if (tiempoRestanteSeg <= 5) RojoGameOver else AmarilloAlerta,
                animationSpec = tween(durationMillis = 300),
                label = "timerColorAesthetic"
            )

            val infiniteTransition = rememberInfiniteTransition(label = "timerPulseAesthetic")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = if (tiempoRestanteSeg <= 5) 1.12f else 1.03f,
                animationSpec = infiniteRepeatable(
                    animation = tween(350, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScaleAesthetic"
            )

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .scale(pulseScale)
                    .background(FondoBase, CircleShape)
                    .border(2.5.dp, timerColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${tiempoRestanteSeg}s",
                    color = TextoBlanco,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun BarraHud(
    label: String,
    valor: Int,
    colorBarra: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (valor / 100f).coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "hudProgressAesthetic"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = TextoSecundario,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$valor%",
                color = colorBarra,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(3.5.dp)),
            color = colorBarra,
            trackColor = FondoInactivoBarras
        )
    }
}

@Composable
private fun BotonRespuestaEstiloP2(
    numero: Int,
    texto: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScaleAesthetic"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) VioletaAccion.copy(alpha = 0.7f) else RespuestaInactiva,
        animationSpec = tween(durationMillis = 100),
        label = "buttonBgAesthetic"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isPressed) CianAccion else VioletaAccion.copy(alpha = 0.8f),
        animationSpec = tween(durationMillis = 100),
        label = "buttonBorderAesthetic"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        color = backgroundColor,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(VioletaAccion, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = numero.toString(),
                        color = TextoBlanco,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = texto,
                    color = TextoBlanco,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Seleccionar",
                tint = TextoSecundario,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F1A, widthDp = 390, heightDp = 844)
@Composable
private fun JuegoScreenPreview() {
    TodoBienTheme {
        JuegoScreen(
            modo = ModoJuego.OLEADAS,
            estabilidad = 60,
            presion = 29,
            rondaActual = 3,
            totalRondas = 10,
            textoSituacion = "Te enterás de que armaron un grupo de difusión para reírse de una foto tuya escrachada.",
            imagenNombre = "img/Assets/S1.jpg",
            opciones = listOf(
                Opcion("Confrontar amablemente", TipoOpcion.Neutral, Efecto(0, -10)),
                Opcion("Pedir ayuda a un adulto", TipoOpcion.Positiva, Efecto(10, -20)),
                Opcion("Ignorar y aislarte", TipoOpcion.Negativa, Efecto(-15, 10))
            ),
            tiempoRestanteSeg = 25,
            onOpcionSelected = {},
            onSalir = {}
        )
    }
}
