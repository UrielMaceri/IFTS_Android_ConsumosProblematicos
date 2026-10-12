package com.example.ifts_android_consumosproblematicos.ui.juego

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ifts_android_consumosproblematicos.R
import com.example.ifts_android_consumosproblematicos.model.Efecto
import com.example.ifts_android_consumosproblematicos.model.ModoJuego
import com.example.ifts_android_consumosproblematicos.model.Opcion
import com.example.ifts_android_consumosproblematicos.model.TipoOpcion
import com.example.ifts_android_consumosproblematicos.ui.theme.*

@Composable
fun JuegoScreen(
    modo: ModoJuego,
    estabilidad: Int,
    presion: Int,
    rondaActual: Int,
    totalRondas: Int,
    textoSituacion: String,
    imagenNombre: String,
    opciones: List<Opcion>,
    tiempoRestanteSeg: Int,
    onOpcionSelected: (Opcion) -> Unit,
    onSalir: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Carga directa del bitmap desde la carpeta assets según la ruta del JSON (ej. "img/Assets/S3.jpeg")
    val imageBitmap = remember(imagenNombre) {
        if (imagenNombre.isNotBlank()) {
            try {
                context.assets.open(imagenNombre).use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                }
            } catch (_: Exception) {
                null
            }
        } else null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoBase)
            .systemBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- 1. HUD SUPERIOR (Barras, Badge de Ronda y Botón Salir) ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Barras de Estado (Estabilidad y Presión Social)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BarraHud(
                        label = "ESTABILIDAD",
                        valor = estabilidad,
                        colorBarra = EstabilidadEmocional
                    )
                    BarraHud(
                        label = "PRESIÓN SOCIAL",
                        valor = presion,
                        colorBarra = PresionSocial
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Insignia de Ronda
                Surface(
                    color = TarjetaSituacion,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, AmarilloAlerta),
                    modifier = Modifier.width(72.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RONDA",
                            color = TextoSecundario,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = rondaActual.toString(),
                            color = TextoBlanco,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón para Salir al Menú
                IconButton(
                    onClick = onSalir,
                    modifier = Modifier
                        .size(40.dp)
                        .background(TarjetaSituacion, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Salir al menú",
                        tint = TextoBlanco
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 2. CONTENEDOR VISUAL / ILUSTRACIÓN ---
        Card(
            colors = CardDefaults.cardColors(containerColor = TarjetaSituacion),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Ilustración de la situación actual",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.s1),
                    contentDescription = "Ilustración de la situación actual",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 3. TARJETA DE SITUACIÓN Y OPCIONES ---
        Card(
            colors = CardDefaults.cardColors(containerColor = TarjetaSituacion),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Cabecera de Situación y Evento
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(VioletaAccion.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(EstabilidadEmocional, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SITUACIÓN",
                            color = InsigniaEducativo,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Evento $rondaActual/$totalRondas",
                        color = TextoSecundario,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Texto del Dilema
                Text(
                    text = textoSituacion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoBlanco,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lista de Opciones (1, 2, 3)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    opciones.forEachIndexed { index, opcion ->
                        BotonRespuestaEstiloP2(
                            numero = index + 1,
                            texto = opcion.texto,
                            onClick = { onOpcionSelected(opcion) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 4. FOOTER / TEMPORIZADOR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo Temporizador
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(FondoBase, CircleShape)
                    .border(2.dp, AmarilloAlerta, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${tiempoRestanteSeg}s",
                    color = TextoBlanco,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun BarraHud(
    label: String,
    valor: Int,
    colorBarra: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = TextoSecundario,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$valor%",
                color = colorBarra,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { (valor / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = colorBarra,
            trackColor = FondoInactivoBarras
        )
    }
}

@Composable
private fun BotonRespuestaEstiloP2(
    numero: Int,
    texto: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = RespuestaInactiva,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, VioletaAccion.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(VioletaAccion, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = numero.toString(),
                        color = TextoBlanco,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = texto,
                    color = TextoBlanco,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Seleccionar",
                tint = TextoSecundario,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F1A, widthDp = 390, heightDp = 844)
@Composable
private fun JuegoScreenPreview() {
    TodoBienTheme {
        JuegoScreen(
            modo = ModoJuego.OLEADAS,
            estabilidad = 60,
            presion = 29,
            rondaActual = 3,
            totalRondas = 10,
            textoSituacion = "Te enterás de que armaron un grupo de difusión para reírse de una foto tuya escrachada.",
            imagenNombre = "img/Assets/S1.jpg",
            opciones = listOf(
                Opcion("Confrontar amablemente", TipoOpcion.Neutral, Efecto(0, -10)),
                Opcion("Pedir ayuda a un adulto", TipoOpcion.Positiva, Efecto(10, -20)),
                Opcion("Ignorar y aislarte", TipoOpcion.Negativa, Efecto(-15, 10))
            ),
            tiempoRestanteSeg = 25,
            onOpcionSelected = {},
            onSalir = {}
        )
    }
}
