package com.example.ifts_android_consumosproblematicos.ui.corazon

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ifts_android_consumosproblematicos.R
import com.example.ifts_android_consumosproblematicos.data.Repos
import com.example.ifts_android_consumosproblematicos.model.ConfigCorazon
import com.example.ifts_android_consumosproblematicos.model.ModoControl
import com.example.ifts_android_consumosproblematicos.ui.theme.FondoBase
import com.example.ifts_android_consumosproblematicos.util.SoundManager

@Composable
fun RecuperacionScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        SoundManager.reproducirMusica(context, R.raw.backgroundminigame)
        onDispose {
            SoundManager.detenerMusica(R.raw.backgroundminigame)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    SoundManager.pausarMusica(R.raw.backgroundminigame)
                }
                Lifecycle.Event.ON_RESUME -> {
                    SoundManager.reproducirMusica(context, R.raw.backgroundminigame)
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var mostrandoIntro by remember { mutableStateOf(true) }

    fun finalizar(gano: Boolean) {
        navController.previousBackStackEntry?.savedStateHandle?.set("recuperacion_gano", gano)
        navController.popBackStack()
    }

    BackHandler {
        finalizar(gano = false)
    }

    if (mostrandoIntro) {
        RecuperacionIntro(
            onListo = { mostrandoIntro = false },
            modifier = modifier
        )
    } else {
        val motor = remember { MotorCorazon(ConfigCorazon.RECUPERACION) }
        val colorArgb by Repos.prefs.colorCorazon.collectAsStateWithLifecycle(initialValue = 0xFFFF4D79.toInt())
        val modoControl by Repos.prefs.modoControl.collectAsStateWithLifecycle(initialValue = ModoControl.TACTIL)
        val hudState by motor.hud.collectAsStateWithLifecycle()

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(FondoBase)
        ) {
            CorazonJuego(
                motor = motor,
                colorCorazon = Color(colorArgb),
                modoControl = modoControl,
                onTermino = { resultado ->
                    finalizar(gano = resultado.gano)
                },
                modifier = Modifier.fillMaxSize()
            )

            // Overlay del HUD de recuperación encima del minijuego
            HudRecuperacion(
                estabilidad = hudState.estabilidad,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}
