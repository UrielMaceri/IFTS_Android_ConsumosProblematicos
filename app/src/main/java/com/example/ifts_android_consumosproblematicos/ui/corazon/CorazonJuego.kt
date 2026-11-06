package com.example.ifts_android_consumosproblematicos.ui.corazon

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ifts_android_consumosproblematicos.model.EventoCorazon
import com.example.ifts_android_consumosproblematicos.model.ModoControl
import com.example.ifts_android_consumosproblematicos.model.ResultadoCorazon
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun CorazonJuego(
    motor: MotorCorazon,
    colorCorazon: Color,
    modoControl: ModoControl,
    pausado: Boolean = false,
    onTermino: (ResultadoCorazon) -> Unit,
    modifier: Modifier = Modifier
) {
    var appEnPrimerPlano by remember { mutableStateOf(true) }
    var resultadoNotificado by remember { mutableStateOf(false) }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        appEnPrimerPlano = false
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        appEnPrimerPlano = true
    }

    // Game loop con withFrameNanos
    LaunchedEffect(pausado, appEnPrimerPlano) {
        if (pausado || !appEnPrimerPlano) return@LaunchedEffect

        var ultimoNanos = 0L
        while (true) {
            withFrameNanos { nanos ->
                if (ultimoNanos != 0L) {
                    val dt = (nanos - ultimoNanos) / 1_000_000_000f
                    motor.actualizar(dt)

                    val res = motor.resultado
                    if (res != null && !resultadoNotificado) {
                        resultadoNotificado = true
                        onTermino(res)
                    }
                }
                ultimoNanos = nanos
            }
        }
    }

    // Efecto visual de destello (Golpe / Recolecto)
    var destelloColor by remember { mutableStateOf<Color?>(null) }
    var destelloAlfa by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(motor) {
        motor.eventos.collect { evento ->
            when (evento) {
                is EventoCorazon.Golpe -> {
                    destelloColor = Color(0x66FF3B30)
                    destelloAlfa = 1f
                }
                is EventoCorazon.Recolecto -> {
                    destelloColor = Color(0x662ECC71)
                    destelloAlfa = 1f
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(destelloAlfa) {
        if (destelloAlfa > 0f) {
            delay(30L)
            destelloAlfa = (destelloAlfa - 0.15f).coerceAtLeast(0f)
        }
    }

    val hudState by motor.hud.collectAsStateWithLifecycle()
    val posCarril = motor.posCarril
    val obstaculos = motor.obstaculos

    // TODO (SPEC-L3): Hook para EfectoInclinacion(activo = modoControl == ModoControl.INCLINACION, onCarril = motor::irACarril)

    // Reutilizar Path del corazón
    val heartPath = remember { Path() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(modoControl) {
                if (modoControl == ModoControl.TACTIL) {
                    coroutineScope {
                        launch {
                            detectTapGestures { offset ->
                                if (offset.x < size.width / 2f) {
                                    motor.mover(-1)
                                } else {
                                    motor.mover(1)
                                }
                            }
                        }
                        launch {
                            var acumuladoDrag = 0f
                            detectHorizontalDragGestures(
                                onDragStart = { acumuladoDrag = 0f },
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    acumuladoDrag += dragAmount
                                    val umbral = size.width / 6f
                                    if (abs(acumuladoDrag) > umbral) {
                                        if (acumuladoDrag > 0) motor.mover(1) else motor.mover(-1)
                                        acumuladoDrag = 0f
                                    }
                                }
                            )
                        }
                    }
                }
            }
    ) {
        val ancho = size.width
        val alto = size.height

        // 1. Líneas de carriles
        val anchoCarril = ancho / 3f
        val xLinea1 = anchoCarril
        val xLinea2 = anchoCarril * 2f

        drawLine(
            color = Color.White.copy(alpha = 0.12f),
            start = Offset(xLinea1, 0f),
            end = Offset(xLinea1, alto),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.12f),
            start = Offset(xLinea2, 0f),
            end = Offset(xLinea2, alto),
            strokeWidth = 2.dp.toPx()
        )

        // 2. Obstáculos
        val anchoObs = anchoCarril * 0.68f
        val altoObs = alto * 0.055f
        val radioObs = CornerRadius(10.dp.toPx(), 10.dp.toPx())

        for (i in obstaculos.indices) {
            val o = obstaculos[i]
            val obsX = ancho * ((o.carril + 0.5f) / 3f)
            val obsY = alto * o.y

            val left = obsX - anchoObs / 2f
            val top = obsY - altoObs / 2f

            if (o.bueno) {
                // Verde +
                drawRoundRect(
                    color = Color(0xFF2ECC71),
                    topLeft = Offset(left, top),
                    size = Size(anchoObs, altoObs),
                    cornerRadius = radioObs
                )
                // Símbolo +
                val largoSimb = 8.dp.toPx()
                drawLine(
                    color = Color.White,
                    start = Offset(obsX - largoSimb, obsY),
                    end = Offset(obsX + largoSimb, obsY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White,
                    start = Offset(obsX, obsY - largoSimb),
                    end = Offset(obsX, obsY + largoSimb),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            } else {
                // Rojo !
                drawRoundRect(
                    color = Color(0xFFE74C3C),
                    topLeft = Offset(left, top),
                    size = Size(anchoObs, altoObs),
                    cornerRadius = radioObs
                )
                // Símbolo !
                drawLine(
                    color = Color.White,
                    start = Offset(obsX, obsY - 8.dp.toPx()),
                    end = Offset(obsX, obsY + 2.dp.toPx()),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = Offset(obsX, obsY + 7.dp.toPx())
                )
            }
        }

        // 3. Corazón del jugador (y = 0.81f)
        val corazonX = ancho * ((posCarril + 0.5f) / 3f)
        val corazonY = alto * 0.81f
        val tamanoCorazon = ancho * 0.16f

        // Anillo del escudo si está activo
        if (hudState.escudo) {
            drawCircle(
                color = Color(0xFF00E5FF),
                radius = tamanoCorazon * 0.75f,
                center = Offset(corazonX, corazonY),
                style = Stroke(width = 5.dp.toPx())
            )
        }

        // Dibujo del corazón
        withTransform({
            translate(left = corazonX, top = corazonY)
        }) {
            heartPath.reset()
            val s = tamanoCorazon
            heartPath.moveTo(0f, s * 0.35f)
            heartPath.cubicTo(-s * 0.6f, -s * 0.15f, -s * 0.6f, -s * 0.75f, 0f, -s * 0.35f)
            heartPath.cubicTo(s * 0.6f, -s * 0.75f, s * 0.6f, -s * 0.15f, 0f, s * 0.35f)
            heartPath.close()

            drawPath(path = heartPath, color = colorCorazon, style = Fill)
        }

        // 4. Destello visual al recolectar o recibir daño
        if (destelloAlfa > 0f && destelloColor != null) {
            drawRect(
                color = destelloColor!!.copy(alpha = destelloAlfa),
                size = size
            )
        }
    }
}
