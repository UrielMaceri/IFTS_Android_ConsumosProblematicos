package com.example.ifts_android_consumosproblematicos.ui.nombre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ifts_android_consumosproblematicos.R
import com.example.ifts_android_consumosproblematicos.data.Repos
import com.example.ifts_android_consumosproblematicos.ui.theme.CianAccion
import com.example.ifts_android_consumosproblematicos.ui.theme.FondoBase
import com.example.ifts_android_consumosproblematicos.ui.theme.TextoBlanco
import com.example.ifts_android_consumosproblematicos.util.FiltroNombre
import kotlinx.coroutines.launch

@Composable
fun NombreScreen(
    editar: Boolean = false,
    onGuardar: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var nombreText by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(editar) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(editar) {
        if (editar) {
            nombreText = Repos.jugador.obtenerNombre() ?: ""
            cargando = false
        }
    }

    val esValido = FiltroNombre.esValido(nombreText)
    val mostrarError = nombreText.isNotEmpty() && !esValido

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoBase)
            .systemBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.corazon_nombre_titulo),
            color = CianAccion,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.corazon_nombre_ayuda),
            color = TextoBlanco.copy(alpha = 0.8f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = nombreText,
            onValueChange = { nombreText = it },
            singleLine = true,
            label = { Text(stringResource(R.string.corazon_nombre_label)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CianAccion,
                unfocusedBorderColor = TextoBlanco.copy(alpha = 0.5f),
                focusedLabelColor = CianAccion,
                unfocusedLabelColor = TextoBlanco.copy(alpha = 0.7f),
                focusedTextColor = TextoBlanco,
                unfocusedTextColor = TextoBlanco
            ),
            isError = mostrarError,
            modifier = Modifier.fillMaxWidth()
        )

        if (mostrarError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.corazon_nombre_error),
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val nombreFinal = nombreText.trim()
                if (FiltroNombre.esValido(nombreFinal)) {
                    scope.launch {
                        Repos.jugador.guardarNombre(nombreFinal)
                        onGuardar(nombreFinal)
                    }
                }
            },
            enabled = esValido && !cargando,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CianAccion,
                contentColor = FondoBase,
                disabledContainerColor = CianAccion.copy(alpha = 0.3f),
                disabledContentColor = FondoBase.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (editar) {
                    stringResource(R.string.corazon_nombre_btn_guardar)
                } else {
                    stringResource(R.string.corazon_nombre_btn_continuar)
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
