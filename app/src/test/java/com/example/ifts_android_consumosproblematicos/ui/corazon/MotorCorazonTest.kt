package com.example.ifts_android_consumosproblematicos.ui.corazon

import com.example.ifts_android_consumosproblematicos.model.ConfigCorazon
import com.example.ifts_android_consumosproblematicos.model.EventoCorazon
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class MotorCorazonTest {

    @Test
    fun arcade_estabilidad100_rojoToca_estabilidad75_combo0() {
        val motor = MotorCorazon(ConfigCorazon.ARCADE)
        assertEquals(100, motor.hud.value.estabilidad)

        motor.agregarObstaculoParaTest(carril = 1, y = 0.81f, bueno = false)
        motor.actualizar(0.01f)

        assertEquals(75, motor.hud.value.estabilidad)
        assertEquals(0, motor.hud.value.combo)
    }

    @Test
    fun arcade_5esquives_escudoTrue_y10esquives_noSeAcumula() = runBlocking {
        val motor = MotorCorazon(ConfigCorazon.ARCADE)
        val eventos = mutableListOf<EventoCorazon>()
        val job = launch {
            motor.eventos.collect { eventos.add(it) }
        }
        yield()

        repeat(5) {
            motor.agregarObstaculoParaTest(carril = 0, y = 0.89f, bueno = false)
            motor.actualizar(0.01f)
        }
        yield()

        assertTrue(motor.hud.value.escudo)
        assertTrue(eventos.contains(EventoCorazon.EscudoActivado))

        val cantidadActivadosAntes = eventos.count { it == EventoCorazon.EscudoActivado }
        repeat(5) {
            motor.agregarObstaculoParaTest(carril = 0, y = 0.89f, bueno = false)
            motor.actualizar(0.01f)
        }
        yield()

        assertTrue(motor.hud.value.escudo)
        val cantidadActivadosDespues = eventos.count { it == EventoCorazon.EscudoActivado }
        assertEquals(cantidadActivadosAntes, cantidadActivadosDespues)

        job.cancel()
    }

    @Test
    fun escudoActivo_rojoToca_escudoFalse_estabilidadSinCambios_combo0_emiteEscudoRoto() = runBlocking {
        val motor = MotorCorazon(ConfigCorazon.ARCADE)
        val eventos = mutableListOf<EventoCorazon>()
        val job = launch {
            motor.eventos.collect { eventos.add(it) }
        }
        yield()

        repeat(5) {
            motor.agregarObstaculoParaTest(carril = 0, y = 0.89f, bueno = false)
            motor.actualizar(0.01f)
        }
        yield()

        val estabilidadPrevia = motor.hud.value.estabilidad
        assertTrue(motor.hud.value.escudo)

        motor.agregarObstaculoParaTest(carril = 1, y = 0.81f, bueno = false)
        motor.actualizar(0.01f)
        yield()

        assertFalse(motor.hud.value.escudo)
        assertEquals(estabilidadPrevia, motor.hud.value.estabilidad)
        assertEquals(0, motor.hud.value.combo)
        assertTrue(eventos.contains(EventoCorazon.EscudoRoto))

        job.cancel()
    }

    @Test
    fun arcade_estabilidad25_rojoSinEscudo_resultadoGanoFalse() {
        val motor = MotorCorazon(ConfigCorazon.ARCADE)

        repeat(3) {
            motor.agregarObstaculoParaTest(carril = 1, y = 0.81f, bueno = false)
            motor.actualizar(0.01f)
        }
        assertEquals(25, motor.hud.value.estabilidad)

        motor.agregarObstaculoParaTest(carril = 1, y = 0.81f, bueno = false)
        motor.actualizar(0.01f)

        assertNotNull(motor.resultado)
        assertEquals(false, motor.resultado?.gano)
    }

    @Test
    fun recuperacion_empiezaEn0_4verdesSeguidos_resultadoGanoTrue() {
        val motor = MotorCorazon(ConfigCorazon.RECUPERACION)
        assertEquals(0, motor.hud.value.estabilidad)

        repeat(4) {
            motor.agregarObstaculoParaTest(carril = 1, y = 0.81f, bueno = true)
            motor.actualizar(0.01f)
        }

        assertNotNull(motor.resultado)
        assertEquals(true, motor.resultado?.gano)
        assertEquals(100, motor.hud.value.estabilidad)
    }

    @Test
    fun recuperacion_rojoTocaConEstabilidad0_resultadoGanoFalse() {
        val motor = MotorCorazon(ConfigCorazon.RECUPERACION)
        assertEquals(0, motor.hud.value.estabilidad)

        motor.agregarObstaculoParaTest(carril = 1, y = 0.81f, bueno = false)
        motor.actualizar(0.01f)

        assertNotNull(motor.resultado)
        assertEquals(false, motor.resultado?.gano)
    }

    @Test
    fun recuperacion_primeraFila_exactamente1VerdeY0Rojos() {
        val motor = MotorCorazon(ConfigCorazon.RECUPERACION)
        motor.actualizar(0.01f)

        val obstaculos = motor.obstaculos
        assertEquals(1, obstaculos.count { it.bueno })
        assertEquals(0, obstaculos.count { !it.bueno })
    }

    @Test
    fun cualquierFilaGenerada_siempreAlMenosUnCarrilSinRojo() {
        repeat(20) { seed ->
            val azar = Random(seed)
            val motor = MotorCorazon(ConfigCorazon.ARCADE, azar)
            motor.actualizar(0.01f)

            val rojos = motor.obstaculos.filter { !it.bueno }
            assertTrue(rojos.size <= 2)
            val carrilesConRojo = rojos.map { it.carril }.toSet()
            assertTrue(carrilesConRojo.size < 3)
        }
    }

    @Test
    fun puntaje_esquiveConCombo5_y_verdeConCombo0() {
        val motor = MotorCorazon(ConfigCorazon.ARCADE)

        // Verde con combo previo 0 -> combo pasa a 1, mult = 1, puntaje = 25 * 1 = 25
        motor.agregarObstaculoParaTest(carril = 1, y = 0.81f, bueno = true)
        motor.actualizar(0.01f)
        assertEquals(25, motor.hud.value.puntaje)

        val motor2 = MotorCorazon(ConfigCorazon.ARCADE)
        // 4 esquives previos -> combo pasa a 4
        repeat(4) {
            motor2.agregarObstaculoParaTest(carril = 0, y = 0.89f, bueno = false)
            motor2.actualizar(0.01f)
        }
        val puntajePrevio = motor2.hud.value.puntaje
        assertEquals(4, motor2.hud.value.combo)

        // 5to esquive -> bump() eleva combo a 5 -> mult = 1 + 5/5 = 2. Esquive suma 10 * 2 = 20
        motor2.agregarObstaculoParaTest(carril = 0, y = 0.89f, bueno = false)
        motor2.actualizar(0.01f)

        assertEquals(5, motor2.hud.value.combo)
        assertEquals(puntajePrevio + 20, motor2.hud.value.puntaje)
    }

    @Test
    fun estabilidad_nuncaSupera100_niBajaDe0_ySoloMultiplosDe25() {
        val motor = MotorCorazon(ConfigCorazon.ARCADE)
        assertEquals(0, motor.hud.value.estabilidad % 25)

        motor.agregarObstaculoParaTest(carril = 1, y = 0.81f, bueno = true)
        motor.actualizar(0.01f)
        assertEquals(100, motor.hud.value.estabilidad)

        repeat(5) {
            motor.agregarObstaculoParaTest(carril = 1, y = 0.81f, bueno = false)
            motor.actualizar(0.01f)
            assertTrue(motor.hud.value.estabilidad >= 0)
            assertEquals(0, motor.hud.value.estabilidad % 25)
        }
        assertEquals(0, motor.hud.value.estabilidad)
    }
}
