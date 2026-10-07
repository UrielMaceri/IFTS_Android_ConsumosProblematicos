package com.example.ifts_android_consumosproblematicos.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ifts_android_consumosproblematicos.ui.theme.TodoBienTheme

@Composable
fun MenuScreen(
    onIniciarPartida: () -> Unit,
    onOpciones: () -> Unit,
    modifier: Modifier = Modifier,
    onEndless: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¿Todo Bien?",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(48.dp))
        BotonMenu("Iniciar Partida", onIniciarPartida)
        // Botón Endless oculto por el momento
        /*
        onEndless?.let {
            Spacer(Modifier.height(16.dp))
            BotonMenu("Endless", it)
        }
        */
        Spacer(Modifier.height(16.dp))
        BotonMenu("Opciones", onOpciones)
    }
}

@Composable
private fun BotonMenu(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(texto)
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuScreenPreview() {
    TodoBienTheme { MenuScreen(onIniciarPartida = {}, onOpciones = {}) }
}
