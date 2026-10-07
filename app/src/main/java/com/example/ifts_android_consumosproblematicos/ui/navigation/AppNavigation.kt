package com.example.ifts_android_consumosproblematicos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ifts_android_consumosproblematicos.model.ModoJuego
import com.example.ifts_android_consumosproblematicos.ui.aviso.AvisoInicialScreen
import com.example.ifts_android_consumosproblematicos.ui.juego.JuegoScreen
import com.example.ifts_android_consumosproblematicos.ui.menu.MenuScreen
import com.example.ifts_android_consumosproblematicos.ui.opciones.OpcionesScreen

object Rutas {
    const val AVISO = "aviso"
    const val MENU = "menu"
    const val OPCIONES = "opciones"
    const val JUEGO = "juego/{modo}"
    fun juego(modo: ModoJuego) = "juego/${modo.name}"
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
            JuegoScreen(modo = modo, onSalir = { navController.popBackStack() })
        }
    }
}
