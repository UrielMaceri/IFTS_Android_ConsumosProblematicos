package com.example.ifts_android_consumosproblematicos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ifts_android_consumosproblematicos.model.ModoJuego
import com.example.ifts_android_consumosproblematicos.ui.aviso.AvisoInicialScreen
import com.example.ifts_android_consumosproblematicos.ui.finalScreens.GameOverScreen
import com.example.ifts_android_consumosproblematicos.ui.finalScreens.SuccessScreen
import com.example.ifts_android_consumosproblematicos.ui.juego.JuegoScreen
import com.example.ifts_android_consumosproblematicos.ui.juego.JuegoScreenViewModel
import com.example.ifts_android_consumosproblematicos.ui.menu.MenuScreen
import com.example.ifts_android_consumosproblematicos.ui.opciones.OpcionesScreen

object Rutas {
    const val AVISO = "aviso"
    const val MENU = "menu"
    const val OPCIONES = "opciones"
    const val JUEGO = "juego/{modo}"
    const val GAME_OVER = "game_over"
    const val SUCCESS = "success/{estabilidad}/{presion}"

    fun juego(modo: ModoJuego) = "juego/${modo.name}"
    fun success(estabilidad: Int, presion: Int) = "success/$estabilidad/$presion"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.AVISO) {
        composable(Rutas.AVISO) {
            AvisoInicialScreen(
                onContinuar = {
                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.AVISO) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.MENU) {
            MenuScreen(
                onIniciarPartida = { navController.navigate(Rutas.juego(ModoJuego.OLEADAS)) },
                onEndless = { navController.navigate(Rutas.juego(ModoJuego.ENDLESS)) },
                onOpciones = { navController.navigate(Rutas.OPCIONES) }
            )
        }
        composable(Rutas.OPCIONES) {
            OpcionesScreen(onVolver = { navController.popBackStack() })
        }
        composable(
            route = Rutas.JUEGO,
            arguments = listOf(navArgument("modo") { type = NavType.StringType })
        ) { entrada ->
            val modo = ModoJuego.valueOf(
                entrada.arguments?.getString("modo") ?: ModoJuego.OLEADAS.name
            )
            val viewModel: JuegoScreenViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.juegoTerminado) {
                if (uiState.juegoTerminado) {
                    if (uiState.esVictoria) {
                        navController.navigate(Rutas.success(uiState.estabilidad, uiState.presion)) {
                            popUpTo(Rutas.JUEGO) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Rutas.GAME_OVER) {
                            popUpTo(Rutas.JUEGO) { inclusive = true }
                        }
                    }
                }
            }

            JuegoScreen(
                modo = modo,
                estabilidad = uiState.estabilidad,
                presion = uiState.presion,
                rondaActual = uiState.rondaActual,
                totalRondas = uiState.totalRondas,
                textoSituacion = uiState.situacionActual?.texto ?: "",
                imagenNombre = uiState.situacionActual?.imagen ?: "",
                opciones = uiState.situacionActual?.opciones ?: emptyList(),
                tiempoRestanteSeg = uiState.tiempoRestanteSeg,
                onOpcionSelected = { opcion -> viewModel.seleccionarOpcion(opcion) },
                onSalir = { navController.popBackStack() }
            )
        }
        composable(Rutas.GAME_OVER) {
            GameOverScreen(
                onReintentar = {
                    navController.navigate(Rutas.juego(ModoJuego.OLEADAS)) {
                        popUpTo(Rutas.MENU)
                    }
                },
                onVolverMenu = {
                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.MENU) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Rutas.SUCCESS,
            arguments = listOf(
                navArgument("estabilidad") { type = NavType.IntType },
                navArgument("presion") { type = NavType.IntType }
            )
        ) { entrada ->
            val estabilidad = entrada.arguments?.getInt("estabilidad") ?: 100
            val presion = entrada.arguments?.getInt("presion") ?: 50

            SuccessScreen(
                estabilidadFinal = estabilidad,
                presionFinal = presion,
                onVolverMenu = {
                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.MENU) { inclusive = true }
                    }
                }
            )
        }
    }
}
