package com.example.ifts_android_consumosproblematicos.ui.corazon

import com.example.ifts_android_consumosproblematicos.model.ConfigCorazon
import com.example.ifts_android_consumosproblematicos.model.EstadoHud
import com.example.ifts_android_consumosproblematicos.model.EventoCorazon
import com.example.ifts_android_consumosproblematicos.model.ResultadoCorazon
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class ObstaculoCorazon(
    val carril: Int,
    var y: Float,
    val bueno: Boolean,
    val icono: Int
)

class MotorCorazon(
    val config: ConfigCorazon,
    private val azar: Random = Random.Default
) {
    private var carrilObjetivo: Int = 1
    var posCarril: Float = 1f
        private set

    private var estabilidad: Int = config.estabilidadInicial
    private var combo: Int = 0
    private var comboMax: Int = 0
    private var puntaje: Int = 0
    private var escudo: Boolean = false
    private var segundosAcumulados: Float = 0f
    private var esPrimeraFila: Boolean = true

    private val _obstaculos = mutableListOf<ObstaculoCorazon>()
    val obstaculos: List<ObstaculoCorazon> get() = _obstaculos

    var resultado: ResultadoCorazon? = null
        private set

    private val _hud = MutableStateFlow(
        EstadoHud(
            estabilidad = estabilidad,
            combo = combo,
            puntaje = puntaje,
            multiplicador = 1,
            escudo = escudo,
            progresoEscudo = 0
        )
    )
    val hud: StateFlow<EstadoHud> = _hud.asStateFlow()

    private val _eventos = MutableSharedFlow<EventoCorazon>(replay = 8, extraBufferCapacity = 8)
    val eventos: SharedFlow<EventoCorazon> = _eventos.asSharedFlow()

    fun mover(delta: Int) {
        irACarril(carrilObjetivo + delta)
    }

    fun irACarril(carril: Int) {
        carrilObjetivo = carril.coerceIn(0, 2)
    }

    internal fun agregarObstaculoParaTest(carril: Int, y: Float, bueno: Boolean) {
        _obstaculos.add(
            ObstaculoCorazon(
                carril = carril.coerceIn(0, 2),
                y = y,
                bueno = bueno,
                icono = 0
            )
        )
    }

    fun actualizar(dt: Float) {
        if (resultado != null) return

        val dtEfectivo = min(dt, 0.05f)
        segundosAcumulados += dtEfectivo

        // Posición suave del carril
        posCarril += (carrilObjetivo.toFloat() - posCarril) * min(1f, dtEfectivo * 28f)

        val nivel = floor(segundosAcumulados / 12f).toInt()
        val velocidad = min(config.velocidadMax, 0.36f + 0.00625f * segundosAcumulados)

        // Generación de obstáculos
        val separacion = max(0.30f, 0.47f - 0.01875f * nivel)
        val minY = _obstaculos.minOfOrNull { it.y }

        if (minY == null || minY >= separacion) {
            val spawnY = if (minY == null) 0f else (minY - separacion)

            if (esPrimeraFila && config.primeraFilaVerde) {
                val carrilVerde = azar.nextInt(3)
                _obstaculos.add(
                    ObstaculoCorazon(
                        carril = carrilVerde,
                        y = spawnY,
                        bueno = true,
                        icono = azar.nextInt(3)
                    )
                )
                esPrimeraFila = false
            } else {
                val carrilLibre = azar.nextInt(3)
                val otrosCarriles = (0..2).filter { it != carrilLibre }
                val probDosRojos = 0.2f + min(0.45f, 0.05f * nivel)
                val dosRojos = azar.nextFloat() < probDosRojos

                if (dosRojos) {
                    _obstaculos.add(
                        ObstaculoCorazon(
                            carril = otrosCarriles[0],
                            y = spawnY,
                            bueno = false,
                            icono = azar.nextInt(4)
                        )
                    )
                    _obstaculos.add(
                        ObstaculoCorazon(
                            carril = otrosCarriles[1],
                            y = spawnY,
                            bueno = false,
                            icono = azar.nextInt(4)
                        )
                    )
                } else {
                    val carrilRojo = otrosCarriles[azar.nextInt(2)]
                    _obstaculos.add(
                        ObstaculoCorazon(
                            carril = carrilRojo,
                            y = spawnY,
                            bueno = false,
                            icono = azar.nextInt(4)
                        )
                    )
                }

                val probVerdeEfectiva = if (estabilidad <= 50) max(config.probVerde, 0.40f) else config.probVerde
                if (azar.nextFloat() < probVerdeEfectiva) {
                    _obstaculos.add(
                        ObstaculoCorazon(
                            carril = carrilLibre,
                            y = spawnY,
                            bueno = true,
                            icono = azar.nextInt(3)
                        )
                    )
                }

                esPrimeraFila = false
            }
        }

        // Mover obstáculos
        val iterator = _obstaculos.iterator()
        while (iterator.hasNext()) {
            val o = iterator.next()
            o.y += velocidad * dtEfectivo

            // Colisión con el corazón (y = 0.81f)
            val enCarril = abs(posCarril - o.carril) < 0.55f
            val enY = abs(o.y - 0.81f) < 0.08f

            if (enCarril && enY) {
                if (o.bueno) {
                    estabilidad = min(100, estabilidad + 25)
                    bump()
                    if (config.puntajeActivo) {
                        val mult = 1 + combo / 5
                        puntaje += 25 * mult
                    }
                    _eventos.tryEmit(EventoCorazon.Recolecto)
                    iterator.remove()

                    if (config.ganaEn != null && estabilidad >= config.ganaEn) {
                        resultado = ResultadoCorazon(
                            gano = true,
                            puntaje = puntaje,
                            comboMax = comboMax,
                            segundos = segundosAcumulados.toInt()
                        )
                    }
                } else {
                    if (escudo) {
                        escudo = false
                        combo = 0
                        _eventos.tryEmit(EventoCorazon.EscudoRoto)
                    } else {
                        estabilidad = max(0, estabilidad - 25)
                        combo = 0
                        _eventos.tryEmit(EventoCorazon.Golpe)

                        if (estabilidad <= 0) {
                            resultado = ResultadoCorazon(
                                gano = false,
                                puntaje = puntaje,
                                comboMax = comboMax,
                                segundos = segundosAcumulados.toInt()
                            )
                        }
                    }
                    iterator.remove()
                }
            } else if (!o.bueno && o.y > 0.88f) {
                // Esquive de obstáculo rojo
                bump()
                if (config.puntajeActivo) {
                    val mult = 1 + combo / 5
                    puntaje += 10 * mult
                }
                _eventos.tryEmit(EventoCorazon.Esquivo(combo))
                iterator.remove()
            } else if (o.y > 1.2f) {
                iterator.remove()
            }
        }

        actualizarHud()
    }

    private fun bump() {
        combo++
        if (combo > comboMax) {
            comboMax = combo
        }
        if (config.escudoActivo && combo % 5 == 0 && !escudo) {
            escudo = true
            _eventos.tryEmit(EventoCorazon.EscudoActivado)
        }
    }

    private fun actualizarHud() {
        val multiplicador = 1 + combo / 5
        val progresoEscudo = combo % 5
        _hud.value = EstadoHud(
            estabilidad = estabilidad,
            combo = combo,
            puntaje = puntaje,
            multiplicador = multiplicador,
            escudo = escudo,
            progresoEscudo = progresoEscudo
        )
    }
}
