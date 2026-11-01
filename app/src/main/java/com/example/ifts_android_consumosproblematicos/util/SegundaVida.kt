package com.example.ifts_android_consumosproblematicos.util

class SegundaVida(private val recargaCadaRondas: Int? = null) {
    var disponible = true
        private set

    fun consumir() {
        check(disponible) { "La segunda vida no está disponible" }
        disponible = false
    }

    fun alCompletarRonda(ronda: Int) {
        if (recargaCadaRondas != null && ronda % recargaCadaRondas == 0) {
            disponible = true
        }
    }
}
