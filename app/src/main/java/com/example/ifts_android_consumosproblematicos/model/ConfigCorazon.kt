package com.example.ifts_android_consumosproblematicos.model

enum class ModoControl { TACTIL, INCLINACION }

data class ConfigCorazon(
    val estabilidadInicial: Int,
    val ganaEn: Int? = null,             // 100 en recuperación, null en arcade
    val escudoActivo: Boolean,
    val puntajeActivo: Boolean,
    val probVerde: Float = 0.18f,
    val velocidadMax: Float = 0.94f,     // alturas de pantalla por segundo
    val primeraFilaVerde: Boolean = false,
) {
    companion object {
        val ARCADE = ConfigCorazon(
            estabilidadInicial = 100, escudoActivo = true, puntajeActivo = true)
        val RECUPERACION = ConfigCorazon(
            estabilidadInicial = 0, ganaEn = 100, escudoActivo = false,
            puntajeActivo = false, probVerde = 0.5f, velocidadMax = 0.65f,
            primeraFilaVerde = true)
    }
}
