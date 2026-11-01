package com.example.ifts_android_consumosproblematicos.data

class FakeJugadorRepository : JugadorRepository {
    private var nombreGuardado: String? = null

    override suspend fun obtenerNombre(): String? = nombreGuardado

    override suspend fun guardarNombre(nombre: String) {
        nombreGuardado = nombre
    }
}
