package com.example.ifts_android_consumosproblematicos.data

interface JugadorRepository {
    suspend fun obtenerNombre(): String?
    suspend fun guardarNombre(nombre: String)
}
