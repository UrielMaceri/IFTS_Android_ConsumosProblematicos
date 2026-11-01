package com.example.ifts_android_consumosproblematicos.model

sealed interface EventoCorazon {
    data object EscudoActivado : EventoCorazon
    data object EscudoRoto : EventoCorazon
    data object Golpe : EventoCorazon
    data object Recolecto : EventoCorazon
    data class Esquivo(val combo: Int) : EventoCorazon
}
