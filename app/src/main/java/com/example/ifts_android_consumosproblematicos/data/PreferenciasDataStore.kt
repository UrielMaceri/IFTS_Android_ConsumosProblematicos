package com.example.ifts_android_consumosproblematicos.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ifts_android_consumosproblematicos.model.ModoControl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferencias")

class PreferenciasDataStore(private val context: Context) : PreferenciasRepo {

    companion object {
        private val CLAVE_COLOR_CORAZON = intPreferencesKey("color_corazon")
        private val CLAVE_MODO_CONTROL = stringPreferencesKey("modo_control")
        private val CLAVE_MEJOR_PUNTAJE = intPreferencesKey("mejor_puntaje")
        private val CLAVE_MEJOR_COMBO = intPreferencesKey("mejor_combo")
        private val CLAVE_RECORD_ENDLESS = intPreferencesKey("record_endless")
        private val CLAVE_MUSICA_ACTIVADA = booleanPreferencesKey("musica_activada")
        private val CLAVE_VIBRACION_ACTIVADA = booleanPreferencesKey("vibracion_activada")

        const val COLOR_DEFECTO = 0xFFFF4D79.toInt()
    }

    override val colorCorazon: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[CLAVE_COLOR_CORAZON] ?: COLOR_DEFECTO
    }

    override val modoControl: Flow<ModoControl> = context.dataStore.data.map { prefs ->
        val nombreModo = prefs[CLAVE_MODO_CONTROL] ?: ModoControl.TACTIL.name
        try {
            ModoControl.valueOf(nombreModo)
        } catch (_: Exception) {
            ModoControl.TACTIL
        }
    }

    override val mejorPuntaje: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[CLAVE_MEJOR_PUNTAJE] ?: 0
    }

    override val mejorCombo: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[CLAVE_MEJOR_COMBO] ?: 0
    }

    override val recordEndless: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[CLAVE_RECORD_ENDLESS] ?: 0
    }

    override val musicaActivada: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[CLAVE_MUSICA_ACTIVADA] ?: true
    }

    override val vibracionActivada: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[CLAVE_VIBRACION_ACTIVADA] ?: true
    }

    override suspend fun setColorCorazon(argb: Int) {
        context.dataStore.edit { prefs ->
            prefs[CLAVE_COLOR_CORAZON] = argb
        }
    }

    override suspend fun setModoControl(modo: ModoControl) {
        context.dataStore.edit { prefs ->
            prefs[CLAVE_MODO_CONTROL] = modo.name
        }
    }

    override suspend fun setMusicaActivada(activada: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[CLAVE_MUSICA_ACTIVADA] = activada
        }
    }

    override suspend fun setVibracionActivada(activada: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[CLAVE_VIBRACION_ACTIVADA] = activada
        }
    }

    override suspend fun registrarArcade(puntaje: Int, combo: Int) {
        context.dataStore.edit { prefs ->
            val actualPuntaje = prefs[CLAVE_MEJOR_PUNTAJE] ?: 0
            if (puntaje > actualPuntaje) {
                prefs[CLAVE_MEJOR_PUNTAJE] = puntaje
            }
            val actualCombo = prefs[CLAVE_MEJOR_COMBO] ?: 0
            if (combo > actualCombo) {
                prefs[CLAVE_MEJOR_COMBO] = combo
            }
        }
    }

    override suspend fun registrarEndless(rondasSuperadas: Int): Boolean {
        var esNuevoRecord = false
        context.dataStore.edit { prefs ->
            val actualRecord = prefs[CLAVE_RECORD_ENDLESS] ?: 0
            if (rondasSuperadas > actualRecord) {
                prefs[CLAVE_RECORD_ENDLESS] = rondasSuperadas
                esNuevoRecord = true
            }
        }
        return esNuevoRecord
    }
}
