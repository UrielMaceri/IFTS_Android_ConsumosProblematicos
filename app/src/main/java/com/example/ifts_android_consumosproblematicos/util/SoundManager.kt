package com.example.ifts_android_consumosproblematicos.util

import android.content.Context
import android.media.MediaPlayer

object SoundManager {

    private var mediaPlayerMusica: MediaPlayer? = null
    var currentResId: Int? = null
        private set

    var musicaHabilitada: Boolean = true
        set(value) {
            field = value
            if (!value) {
                detenerMusica()
            }
        }

    var efectosHabilitados: Boolean = true
    var vibracionHabilitada: Boolean = true

    /**
     * Inicia la reproducción de música de fondo en bucle (loop).
     * Si ya había una canción sonando, la detiene antes de iniciar la nueva.
     */
    fun reproducirMusica(context: Context, resId: Int) {
        if (!musicaHabilitada) return

        if (currentResId == resId && mediaPlayerMusica != null) {
            try {
                if (mediaPlayerMusica?.isPlaying == false) {
                    mediaPlayerMusica?.start()
                }
                return
            } catch (_: Exception) {}
        }

        detenerMusica()

        try {
            mediaPlayerMusica = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                start()
            }
            currentResId = resId
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Detiene la música de fondo actual y libera los recursos de memoria.
     */
    fun detenerMusica(resId: Int? = null) {
        if (resId != null && currentResId != null && currentResId != resId) {
            return
        }
        try {
            mediaPlayerMusica?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayerMusica = null
        currentResId = null
    }

    /**
     * Pausa la música de fondo sin liberarla de memoria.
     */
    fun pausarMusica(resId: Int? = null) {
        if (resId != null && currentResId != null && currentResId != resId) {
            return
        }
        try {
            mediaPlayerMusica?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Reanuda la música de fondo si estaba pausada.
     */
    fun reanudarMusica(resId: Int? = null) {
        if (!musicaHabilitada) return
        if (resId != null && currentResId != null && currentResId != resId) {
            return
        }
        try {
            mediaPlayerMusica?.let { player ->
                if (!player.isPlaying) {
                    player.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    /**
     * Realiza un patrón de vibración tipo "latido de corazón" ([0, 100, 100, 200] ms)
     * cuando la estabilidad es baja (<= 25%), respetando las preferencias de vibración.
     */
    fun vibrarLatidoCorazon(context: Context) {
        if (!vibracionHabilitada) return

        try {
            val patrones = longArrayOf(0, 100, 100, 200)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    android.os.VibrationEffect.createWaveform(patrones, -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator?.vibrate(android.os.VibrationEffect.createWaveform(patrones, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(patrones, -1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}