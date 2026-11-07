package com.example.ifts_android_consumosproblematicos.ui.corazon

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun EfectoInclinacion(
    activo: Boolean,
    onCarril: (Int) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val sensorManager = remember(context) {
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    }
    val acelerometro = remember(sensorManager) {
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    val currentOnCarril by rememberUpdatedState(onCarril)

    DisposableEffect(activo, lifecycleOwner, sensorManager, acelerometro) {
        if (!activo || sensorManager == null || acelerometro == null) {
            return@DisposableEffect onDispose {}
        }

        var xFiltrado = 0f
        var primerLectura = true
        var carrilActual = 1

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

                val rawX = event.values[0]
                if (primerLectura) {
                    xFiltrado = rawX
                    primerLectura = false
                } else {
                    xFiltrado = 0.8f * xFiltrado + 0.2f * rawX
                }

                val nuevoCarril = when (carrilActual) {
                    0 -> { // Estaba en carril 0 (izquierda)
                        if (xFiltrado < 2.0f) {
                            if (xFiltrado < -2.5f) 2 else 1
                        } else {
                            0
                        }
                    }
                    2 -> { // Estaba en carril 2 (derecha)
                        if (xFiltrado > -2.0f) {
                            if (xFiltrado > 2.5f) 0 else 1
                        } else {
                            2
                        }
                    }
                    else -> { // Estaba en carril 1 (centro)
                        if (xFiltrado > 2.5f) {
                            0
                        } else if (xFiltrado < -2.5f) {
                            2
                        } else {
                            1
                        }
                    }
                }

                if (nuevoCarril != carrilActual) {
                    carrilActual = nuevoCarril
                    currentOnCarril(nuevoCarril)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        var estaRegistrado = false

        fun registrar() {
            if (!estaRegistrado) {
                sensorManager.registerListener(listener, acelerometro, SensorManager.SENSOR_DELAY_GAME)
                estaRegistrado = true
            }
        }

        fun desregistrar() {
            if (estaRegistrado) {
                sensorManager.unregisterListener(listener)
                estaRegistrado = false
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> registrar()
                Lifecycle.Event.ON_PAUSE -> desregistrar()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            registrar()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            desregistrar()
        }
    }
}
