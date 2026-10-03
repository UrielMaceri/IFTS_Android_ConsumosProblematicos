package com.example.ifts_android_consumosproblematicos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TodoBienColorScheme = darkColorScheme(
    primary = CianAccion,
    onPrimary = FondoBase,
    secondary = VioletaAccion,
    onSecondary = TextoBlanco,
    tertiary = AmarilloAlerta,
    background = FondoBase,
    onBackground = TextoBlanco,
    surface = TarjetaSituacion,
    onSurface = TextoBlanco,
    error = RojoGameOver,
    onError = TextoBlanco
)

@Composable
fun TodoBienTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // El simulador "¿Todo Bien?" utiliza una identidad visual oscura fija orientada al entorno escolar
    val colorScheme = TodoBienColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
