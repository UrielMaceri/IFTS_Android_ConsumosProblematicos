package com.example.ifts_android_consumosproblematicos.util

import android.content.Context
import android.media.MediaPlayer

object SoundManager {

    private var mediaPlayerMusica: MediaPlayer? = null
    var musicaHabilitada: Boolean = true
    var efectosHabilitados: Boolean = true

    /**
     * Inicia la reproducción de música de fondo en bucle (loop).
     * Si ya había una canción sonando, la detiene antes de iniciar la nueva.
     */
    fun reproducirMusica(context: Context, resId: Int) {
        if (!musicaHabilitada) return

        detenerMusica()

        try {
            mediaPlayerMusica = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Detiene la música de fondo actual y libera los recursos de memoria.
     */
    fun detenerMusica() {
        mediaPlayerMusica?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
        }
        mediaPlayerMusica = null
    }

    /**
     * Reproduce un efecto de sonido puntual (un solo disparo)
     * y libera la memoria automáticamente al finalizar el sonido.
     */
    fun reproducirEfecto(context: Context, resId: Int, volumen: Float = 1.0f) {
        if (!efectosHabilitados) return

        try {
            val player = MediaPlayer.create(context, resId)
            // Ajustamos el volumen (izquierdo y derecho)
            player?.setVolume(volumen, volumen)
            player?.setOnCompletionListener { mp ->
                mp.release()
            }
            player?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}