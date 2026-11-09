package com.example.ifts_android_consumosproblematicos.ui.corazon

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ifts_android_consumosproblematicos.data.Repos
import com.example.ifts_android_consumosproblematicos.model.ConfigCorazon
import com.example.ifts_android_consumosproblematicos.model.ModoControl
import com.example.ifts_android_consumosproblematicos.model.ResultadoCorazon
import com.example.ifts_android_consumosproblematicos.ui.theme.FondoBase

@Composable
fun ArcadeScreen(
    onTermino: (ResultadoCorazon) -> Unit = {},
    onVolverMenu: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val motor = remember { MotorCorazon(ConfigCorazon.ARCADE) }

    val colorArgb by Repos.prefs.colorCorazon.collectAsState(initial = 0xFFFF4D79.toInt())
    val modoControl by Repos.prefs.modoControl.collectAsState(initial = ModoControl.TACTIL)
    val colorCorazon = remember(colorArgb) { Color(colorArgb) }

    val hudState by motor.hud.collectAsStateWithLifecycle()

    BackHandler {
        onVolverMenu()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FondoBase)
    ) {
        // Juego principal
        CorazonJuego(
            motor = motor,
            colorCorazon = colorCorazon,
            modoControl = modoControl,
            onTermino = onTermino,
            modifier = Modifier.fillMaxSize()
        )

        // HUD overlay (de HudCorazon.kt)
        HudArcade(
            hud = hudState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .systemBarsPadding()
        )
    }
}
