package com.example.ifts_android_consumosproblematicos.ui.corazon

import android.graphics.BitmapFactory
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ifts_android_consumosproblematicos.model.EventoCorazon
import com.example.ifts_android_consumosproblematicos.model.ModoControl
import com.example.ifts_android_consumosproblematicos.model.ResultadoCorazon
import com.example.ifts_android_consumosproblematicos.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

data class TextoFlotante(
    val id: Long,
    val texto: String,
    val carrilPos: Float,
    val color: Color,
    var y: Float = 0.74f,
    var offsetY: Float = 0f,
    var alfa: Float = 1f
)

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
    var frameTick by remember { mutableLongStateOf(0L) }

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
                    frameTick = nanos

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

    // Textos flotantes inspiracionales / advertencias
    val frasesBuenas = remember { listOf("¡Buena vibra!", "¡Empatía!", "¡Respirá!", "¡Fuerza!", "¡Vos podés!") }
    val frasesMalas = remember { listOf("¡Cuidado!", "¡Presión!", "¡Estrés!", "¡Tensión!") }
    var idSecuencia by remember { mutableLongStateOf(0L) }
    val textosFlotantes = remember { mutableStateListOf<TextoFlotante>() }
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(motor) {
        motor.eventos.collect { evento ->
            val carril = motor.posCarril
            idSecuencia++
            when (evento) {
                is EventoCorazon.Recolecto -> {
                    destelloColor = Color(0x662ECC71)
                    destelloAlfa = 1f
                    textosFlotantes.add(
                        TextoFlotante(
                            id = idSecuencia,
                            texto = frasesBuenas.random(),
                            carrilPos = carril,
                            color = Color(0xFF2ECC71)
                        )
                    )
                }
                is EventoCorazon.Golpe -> {
                    destelloColor = Color(0x66FF3B30)
                    destelloAlfa = 1f
                    textosFlotantes.add(
                        TextoFlotante(
                            id = idSecuencia,
                            texto = frasesMalas.random(),
                            carrilPos = carril,
                            color = Color(0xFFE74C3C)
                        )
                    )
                }
                is EventoCorazon.EscudoActivado -> {
                    textosFlotantes.add(
                        TextoFlotante(
                            id = idSecuencia,
                            texto = "¡ESCUDO ACTIVADO!",
                            carrilPos = carril,
                            color = Color(0xFF00E5FF)
                        )
                    )
                }
                is EventoCorazon.EscudoRoto -> {
                    destelloColor = Color(0x6600E5FF)
                    destelloAlfa = 1f
                    textosFlotantes.add(
                        TextoFlotante(
                            id = idSecuencia,
                            texto = "¡No me afecta!",
                            carrilPos = carril,
                            color = Color(0xFF00E5FF)
                        )
                    )
                }
                else -> {}
            }
        }
    }

    // Actualizar movimiento de textos flotantes (duración ampliada a ~1.8s)
    LaunchedEffect(frameTick) {
        if (textosFlotantes.isNotEmpty()) {
            val iterator = textosFlotantes.iterator()
            while (iterator.hasNext()) {
                val tf = iterator.next()
                tf.offsetY += 2.2f
                tf.alfa -= 0.009f
                if (tf.alfa <= 0f) {
                    iterator.remove()
                }
            }
        }
    }

    LaunchedEffect(destelloAlfa) {
        if (destelloAlfa > 0f) {
            delay(30L)
            destelloAlfa = (destelloAlfa - 0.15f).coerceAtLeast(0f)
        }
    }

    val context = LocalContext.current

    // Carga de imágenes de instrucción de controles desde assets
    val bitmapTouch = remember(context) {
        try {
            context.assets.open("touch.png").use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
            }
        } catch (_: Exception) {
            null
        }
    }

    val bitmapRotate = remember(context) {
        try {
            context.assets.open("rotate.png").use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
            }
        } catch (_: Exception) {
            null
        }
    }

    // Overlay de instrucciones de control (3 segundos de duración, semitransparente)
    var alfaOverlay by remember { mutableFloatStateOf(0.60f) }
    var mostrandoOverlayControles by remember { mutableStateOf(true) }

    LaunchedEffect(pausado, appEnPrimerPlano) {
        if (!pausado && appEnPrimerPlano && mostrandoOverlayControles) {
            delay(1800L) // Permanece visible 1.8s a 0.60 de opacidad
            var tiempoDesvanecer = 1200L
            while (tiempoDesvanecer > 0) {
                delay(30L)
                tiempoDesvanecer -= 30L
                alfaOverlay = (tiempoDesvanecer / 1200f * 0.60f).coerceAtLeast(0f)
            }
            mostrandoOverlayControles = false
        }
    }

    val hudState by motor.hud.collectAsStateWithLifecycle()
    val estabilidadMinijuego = hudState.estabilidad

    // Latido hápter cuando la estabilidad es baja (<= 25%)
    LaunchedEffect(estabilidadMinijuego, pausado, appEnPrimerPlano) {
        if (!pausado && appEnPrimerPlano && estabilidadMinijuego in 1..25) {
            while (true) {
                SoundManager.vibrarLatidoCorazon(context)
                delay(1200L)
            }
        }
    }

    EfectoInclinacion(
        activo = modoControl == ModoControl.INCLINACION && !pausado,
        onCarril = motor::irACarril
    )

    // Reutilizar Path del corazón
    val heartPath = remember { Path() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(modoControl) {
                if (modoControl == ModoControl.TACTIL) {
                    detectTapGestures(
                        onTap = { offset ->
                            if (offset.x < size.width / 2f) {
                                motor.mover(-1)
                            } else {
                                motor.mover(1)
                            }
                        }
                    )
                }
            }
            .pointerInput(modoControl) {
                if (modoControl == ModoControl.TACTIL) {
                    var acumuladoDrag = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { acumuladoDrag = 0f },
                        onHorizontalDrag = { change, dragAmount ->
                            acumuladoDrag += dragAmount
                            val umbral = size.width / 8f
                            if (abs(acumuladoDrag) > umbral) {
                                if (acumuladoDrag > 0) motor.mover(1) else motor.mover(-1)
                                acumuladoDrag = 0f
                                change.consume()
                            }
                        }
                    )
                }
            }
    ) {
        @Suppress("UNUSED_VARIABLE")
        val tick = frameTick

        val posCarril = motor.posCarril
        val obstaculos = motor.obstaculos

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

        // 5. Textos flotantes inspiracionales / advertencias
        for (i in textosFlotantes.indices) {
            val tf = textosFlotantes[i]
            if (tf.alfa > 0f) {
                val layoutResult = textMeasurer.measure(
                    text = AnnotatedString(tf.texto),
                    style = TextStyle(
                        color = tf.color.copy(alpha = tf.alfa.coerceIn(0f, 1f)),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.8f * tf.alfa.coerceIn(0f, 1f)),
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
                val textX = (ancho * ((tf.carrilPos + 0.5f) / 3f)) - (layoutResult.size.width / 2f)
                val textY = (alto * tf.y) - tf.offsetY

                drawText(
                    textLayoutResult = layoutResult,
                    topLeft = Offset(textX, textY)
                )
            }
        }

        // 6. Overlay de instrucciones de control (3 segundos, semitransparente)
        if (alfaOverlay > 0f) {
            val imgBitmap = if (modoControl == ModoControl.TACTIL) bitmapTouch else bitmapRotate
            if (imgBitmap != null) {
                val targetWidth = (ancho * 0.22f).toInt()
                val targetHeight = (targetWidth * (imgBitmap.height.toFloat() / imgBitmap.width.toFloat())).toInt()
                val targetSize = IntSize(targetWidth, targetHeight)
                val centerY = (alto * 0.48f) - (targetHeight / 2f)

                // Lado izquierdo
                val leftX = (ancho * 0.20f) - (targetWidth / 2f)
                drawImage(
                    image = imgBitmap,
                    dstOffset = IntOffset(leftX.toInt(), centerY.toInt()),
                    dstSize = targetSize,
                    alpha = alfaOverlay
                )

                // Lado derecho (flippeado horizontalmente para el acelerómetro)
                val rightX = (ancho * 0.80f) - (targetWidth / 2f)
                if (modoControl == ModoControl.INCLINACION) {
                    val rightCenterX = ancho * 0.80f
                    val rightCenterY = centerY + (targetHeight / 2f)
                    withTransform({
                        scale(scaleX = -1f, scaleY = 1f, pivot = Offset(rightCenterX, rightCenterY))
                    }) {
                        drawImage(
                            image = imgBitmap,
                            dstOffset = IntOffset(rightX.toInt(), centerY.toInt()),
                            dstSize = targetSize,
                            alpha = alfaOverlay
                        )
                    }
                } else {
                    drawImage(
                        image = imgBitmap,
                        dstOffset = IntOffset(rightX.toInt(), centerY.toInt()),
                        dstSize = targetSize,
                        alpha = alfaOverlay
                    )
                }

                // Texto explicativo debajo del icono
                val textoIndicador = if (modoControl == ModoControl.TACTIL) "Toca aquí" else "Mueve"
                val textResult = textMeasurer.measure(
                    text = AnnotatedString(textoIndicador),
                    style = TextStyle(
                        color = Color.White.copy(alpha = alfaOverlay),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.8f * alfaOverlay),
                            offset = Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    )
                )

                val textY = centerY + targetHeight + 6.dp.toPx()
                val leftTextX = (ancho * 0.20f) - (textResult.size.width / 2f)
                val rightTextX = (ancho * 0.80f) - (textResult.size.width / 2f)

                drawText(textLayoutResult = textResult, topLeft = Offset(leftTextX, textY))
                drawText(textLayoutResult = textResult, topLeft = Offset(rightTextX, textY))
            }
        }
    }
}
