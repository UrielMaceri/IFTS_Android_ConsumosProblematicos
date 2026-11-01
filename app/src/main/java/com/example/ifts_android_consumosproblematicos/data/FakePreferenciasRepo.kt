package com.example.ifts_android_consumosproblematicos.data

import com.example.ifts_android_consumosproblematicos.model.ModoControl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePreferenciasRepo : PreferenciasRepo {
    private val _colorCorazon = MutableStateFlow(0xFFFF4D79.toInt())
    private val _modoControl = MutableStateFlow(ModoControl.TACTIL)
    private val _mejorPuntaje = MutableStateFlow(0)
    private val _mejorCombo = MutableStateFlow(0)
    private val _recordEndless = MutableStateFlow(0)

    override val colorCorazon: Flow<Int> = _colorCorazon.asStateFlow()
    override val modoControl: Flow<ModoControl> = _modoControl.asStateFlow()
    override val mejorPuntaje: Flow<Int> = _mejorPuntaje.asStateFlow()
    override val mejorCombo: Flow<Int> = _mejorCombo.asStateFlow()
    override val recordEndless: Flow<Int> = _recordEndless.asStateFlow()

    override suspend fun setColorCorazon(argb: Int) {
        _colorCorazon.value = argb
    }

    override suspend fun setModoControl(modo: ModoControl) {
        _modoControl.value = modo
    }

    override suspend fun registrarArcade(puntaje: Int, combo: Int) {
        if (puntaje > _mejorPuntaje.value) {
            _mejorPuntaje.value = puntaje
        }
        if (combo > _mejorCombo.value) {
            _mejorCombo.value = combo
        }
    }

    override suspend fun registrarEndless(rondasSuperadas: Int): Boolean {
        return if (rondasSuperadas > _recordEndless.value) {
            _recordEndless.value = rondasSuperadas
            true
        } else {
            false
        }
    }
}
