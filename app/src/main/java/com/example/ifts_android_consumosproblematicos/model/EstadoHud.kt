package com.example.ifts_android_consumosproblematicos.model

data class EstadoHud(
    val estabilidad: Int,
    val combo: Int,
    val puntaje: Int,
    val multiplicador: Int,
    val escudo: Boolean,
    val progresoEscudo: Int,     // combo % 5 (0..4)
)
