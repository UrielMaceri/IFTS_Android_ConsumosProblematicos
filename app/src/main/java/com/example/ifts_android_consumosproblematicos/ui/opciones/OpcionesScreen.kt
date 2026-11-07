package com.example.ifts_android_consumosproblematicos.ui.opciones

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ifts_android_consumosproblematicos.R
import com.example.ifts_android_consumosproblematicos.data.Repos
import com.example.ifts_android_consumosproblematicos.model.ModoControl
import com.example.ifts_android_consumosproblematicos.ui.theme.CianAccion
import com.example.ifts_android_consumosproblematicos.ui.theme.FondoBase
import com.example.ifts_android_consumosproblematicos.ui.theme.TextoBlanco
import com.example.ifts_android_consumosproblematicos.ui.theme.TodoBienTheme
import kotlinx.coroutines.launch

@Composable
fun OpcionesScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val tieneAcelerometro = remember(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
    }

    val musicaActivada by Repos.prefs.musicaActivada.collectAsState(initial = true)
    val vibracionActivada by Repos.prefs.vibracionActivada.collectAsState(initial = true)
    val modoControl by Repos.prefs.modoControl.collectAsState(initial = ModoControl.TACTIL)

    LaunchedEffect(tieneAcelerometro, modoControl) {
        if (!tieneAcelerometro && modoControl == ModoControl.INCLINACION) {
            Repos.prefs.setModoControl(ModoControl.TACTIL)
        }
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoBase)
            .systemBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500)) { -it / 2 }
        ) {
            Text(
                text = stringResource(R.string.corazon_opciones_titulo),
                style = MaterialTheme.typography.displayMedium,
                color = CianAccion,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 150)) + slideInVertically(animationSpec = tween(600, delayMillis = 150)) { it / 3 },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Switch Música
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.corazon_opciones_musica),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoBlanco
                    )
                    Switch(
                        checked = musicaActivada,
                        onCheckedChange = { activada ->
                            scope.launch { Repos.prefs.setMusicaActivada(activada) }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Switch Vibración
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.corazon_opciones_vibracion),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoBlanco
                    )
                    Switch(
                        checked = vibracionActivada,
                        onCheckedChange = { activada ->
                            scope.launch { Repos.prefs.setVibracionActivada(activada) }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección Control del corazón
                Text(
                    text = stringResource(R.string.corazon_opciones_control_titulo),
                    style = MaterialTheme.typography.titleLarge,
                    color = CianAccion,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Opción TÁCTIL
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (modoControl == ModoControl.TACTIL),
                            onClick = {
                                scope.launch { Repos.prefs.setModoControl(ModoControl.TACTIL) }
                            }
                        )
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (modoControl == ModoControl.TACTIL),
                        onClick = {
                            scope.launch { Repos.prefs.setModoControl(ModoControl.TACTIL) }
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = CianAccion,
                            unselectedColor = TextoBlanco.copy(alpha = 0.6f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.corazon_opciones_control_tactil),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextoBlanco
                    )
                }

                // Opción INCLINACIÓN
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (modoControl == ModoControl.INCLINACION),
                                enabled = tieneAcelerometro,
                                onClick = {
                                    if (tieneAcelerometro) {
                                        scope.launch { Repos.prefs.setModoControl(ModoControl.INCLINACION) }
                                    }
                                }
                            )
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (modoControl == ModoControl.INCLINACION),
                            enabled = tieneAcelerometro,
                            onClick = {
                                if (tieneAcelerometro) {
                                    scope.launch { Repos.prefs.setModoControl(ModoControl.INCLINACION) }
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = CianAccion,
                                unselectedColor = TextoBlanco.copy(alpha = 0.6f),
                                disabledSelectedColor = CianAccion.copy(alpha = 0.4f),
                                disabledUnselectedColor = TextoBlanco.copy(alpha = 0.2f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.corazon_opciones_control_inclinacion),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (tieneAcelerometro) {
                                TextoBlanco
                            } else {
                                TextoBlanco.copy(alpha = 0.4f)
                            }
                        )
                    }

                    if (!tieneAcelerometro) {
                        Text(
                            text = stringResource(R.string.corazon_opciones_sin_acelerometro),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 40.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(700, delayMillis = 300)) + slideInVertically(animationSpec = tween(700, delayMillis = 300)) { it / 2 },
            modifier = Modifier.fillMaxWidth()
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.96f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "volverButtonScale"
            )

            Button(
                onClick = onVolver,
                interactionSource = interactionSource,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CianAccion,
                    contentColor = FondoBase
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(scale)
            ) {
                Text(
                    text = stringResource(R.string.corazon_opciones_volver),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OpcionesScreenPreview() {
    TodoBienTheme {
        OpcionesScreen(onVolver = {})
    }
}
