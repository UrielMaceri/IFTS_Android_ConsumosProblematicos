package com.example.ifts_android_consumosproblematicos.data

import com.example.ifts_android_consumosproblematicos.model.ModoControl
import kotlinx.coroutines.flow.Flow

interface PreferenciasRepo {
    val colorCorazon: Flow<Int>          // ARGB, por defecto 0xFFFF4D79
    val modoControl: Flow<ModoControl>   // por defecto TACTIL
    val mejorPuntaje: Flow<Int>
    val mejorCombo: Flow<Int>
    val recordEndless: Flow<Int>
    suspend fun setColorCorazon(argb: Int)
    suspend fun setModoControl(modo: ModoControl)
    suspend fun registrarArcade(puntaje: Int, combo: Int)
    suspend fun registrarEndless(rondasSuperadas: Int): Boolean  // true si es récord nuevo
}
