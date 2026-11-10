package com.example.ifts_android_consumosproblematicos.data

import com.example.ifts_android_consumosproblematicos.model.ModoControl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface PreferenciasRepo {
    val colorCorazon: Flow<Int>          // ARGB, por defecto 0xFFFF4D79
    val modoControl: Flow<ModoControl>   // por defecto TACTIL
    val mejorPuntaje: Flow<Int>
    val mejorCombo: Flow<Int>
    val recordEndless: Flow<Int>
    val musicaActivada: Flow<Boolean> get() = flowOf(true)
    val efectosActivados: Flow<Boolean> get() = flowOf(true)
    val vibracionActivada: Flow<Boolean> get() = flowOf(true)
    val oleadasCompletado: Flow<Boolean> get() = flowOf(false)

    suspend fun setColorCorazon(argb: Int)
    suspend fun setModoControl(modo: ModoControl)
    suspend fun setMusicaActivada(activada: Boolean) {}
    suspend fun setEfectosActivados(activada: Boolean) {}
    suspend fun setVibracionActivada(activada: Boolean) {}
    suspend fun setOleadasCompletado(completado: Boolean) {}
    suspend fun registrarArcade(puntaje: Int, combo: Int)
    suspend fun registrarEndless(rondasSuperadas: Int): Boolean  // true si es récord nuevo
}
