package com.example.ifts_android_consumosproblematicos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ifts_android_consumosproblematicos.data.Repos
import com.example.ifts_android_consumosproblematicos.ui.navigation.AppNavigation
import com.example.ifts_android_consumosproblematicos.ui.theme.TodoBienTheme
import com.example.ifts_android_consumosproblematicos.util.SoundManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Repos.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            val musicaActivada by Repos.prefs.musicaActivada.collectAsStateWithLifecycle(initialValue = true)
            val efectosActivados by Repos.prefs.efectosActivados.collectAsStateWithLifecycle(initialValue = true)
            val vibracionActivada by Repos.prefs.vibracionActivada.collectAsStateWithLifecycle(initialValue = true)

            LaunchedEffect(musicaActivada) {
                SoundManager.musicaHabilitada = musicaActivada
            }

            LaunchedEffect(efectosActivados) {
                SoundManager.efectosHabilitados = efectosActivados
            }

            LaunchedEffect(vibracionActivada) {
                SoundManager.vibracionHabilitada = vibracionActivada
            }

            TodoBienTheme {
                AppNavigation()
            }
        }
    }
}
