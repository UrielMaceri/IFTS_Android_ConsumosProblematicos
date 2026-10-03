package com.example.ifts_android_consumosproblematicos.model

data class Situacion(
    val id: Int,
    val texto: String,
    val imagen: String,
    val tiempoLimiteSeg: Int,
    val opciones: List<Opcion>
)