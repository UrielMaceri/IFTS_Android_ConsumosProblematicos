package com.example.ifts_android_consumosproblematicos.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SegundaVidaTest {

    @Test
    fun partidaNueva_disponibleTrue() {
        val sv = SegundaVida(20)
        assertTrue(sv.disponible)
    }

    @Test
    fun seUsaEnRonda1_completas2a19_sigueFalse() {
        val sv = SegundaVida(20)
        sv.consumir()
        assertFalse(sv.disponible)

        for (ronda in 2..19) {
            sv.alCompletarRonda(ronda)
            assertFalse(sv.disponible)
        }
    }

    @Test
    fun seUsaEnRonda1_completaRonda20_pasaATrue() {
        val sv = SegundaVida(20)
        sv.consumir()
        assertFalse(sv.disponible)

        for (ronda in 2..19) {
            sv.alCompletarRonda(ronda)
        }
        assertFalse(sv.disponible)

        sv.alCompletarRonda(20)
        assertTrue(sv.disponible)
    }

    @Test
    fun noSeUsaRondas1a20_sigueTrue_unaSola() {
        val sv = SegundaVida(20)
        assertTrue(sv.disponible)

        for (ronda in 1..20) {
            sv.alCompletarRonda(ronda)
            assertTrue(sv.disponible)
        }
    }

    @Test
    fun noSeUsaRondas1a40_sigueTrue_noAcumula() {
        val sv = SegundaVida(20)
        for (ronda in 1..40) {
            sv.alCompletarRonda(ronda)
        }
        assertTrue(sv.disponible)

        sv.consumir()
        assertFalse(sv.disponible)
    }

    @Test
    fun seUsaEnRonda21_completas22a39_false_completa40_true() {
        val sv = SegundaVida(20)
        for (ronda in 1..20) {
            sv.alCompletarRonda(ronda)
        }
        assertTrue(sv.disponible)

        sv.consumir()
        assertFalse(sv.disponible)

        for (ronda in 22..39) {
            sv.alCompletarRonda(ronda)
            assertFalse(sv.disponible)
        }

        sv.alCompletarRonda(40)
        assertTrue(sv.disponible)
    }

    @Test(expected = IllegalStateException::class)
    fun consumirDosVecesSeguidas_lanzaExcepcion() {
        val sv = SegundaVida(20)
        sv.consumir()
        sv.consumir()
    }

    @Test
    fun segundaVidaOleadasNull_alCompletarRonda20_nuncaRecarga() {
        val sv = SegundaVida(null)
        assertTrue(sv.disponible)

        sv.consumir()
        assertFalse(sv.disponible)

        for (ronda in 1..40) {
            sv.alCompletarRonda(ronda)
            assertFalse(sv.disponible)
        }
    }
}
