package com.example.ifts_android_consumosproblematicos.model

data class EstadoJugador(
    val estabilidad: Int = ESTABILIDAD_INICIAL,
    val presion: Int = PRESION_INICIAL
) {
    val gameOver: Boolean
        get() = estabilidad <= MIN_VALOR || presion >= MAX_VALOR

    fun aplicar(efecto: Efecto) = copy(
        estabilidad = (estabilidad + efecto.estabilidad).coerceIn(MIN_VALOR, MAX_VALOR),
        presion = (presion + efecto.presion).coerceIn(MIN_VALOR, MAX_VALOR)
    )

    fun reiniciar() = EstadoJugador()

    companion object {
        const val MIN_VALOR = 0
        const val MAX_VALOR = 100
        const val ESTABILIDAD_INICIAL = 100
        const val PRESION_INICIAL = 50
    }
}