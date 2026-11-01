package com.example.ifts_android_consumosproblematicos.ui.corazon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ifts_android_consumosproblematicos.ui.theme.FondoBase
import com.example.ifts_android_consumosproblematicos.ui.theme.TextoBlanco

@Composable
fun ArcadeSetupScreen(
    onIniciarArcade: () -> Unit = {},
    onVerRanking: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FondoBase)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Próximamente",
            color = TextoBlanco,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
