package com.example.ifts_android_consumosproblematicos.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ifts_android_consumosproblematicos.data.Repos
import com.example.ifts_android_consumosproblematicos.model.ModoJuego
import com.example.ifts_android_consumosproblematicos.ui.aviso.AvisoInicialScreen
import com.example.ifts_android_consumosproblematicos.ui.corazon.*
import com.example.ifts_android_consumosproblematicos.ui.finalScreens.GameOverScreen
import com.example.ifts_android_consumosproblematicos.ui.finalScreens.SuccessScreen
import com.example.ifts_android_consumosproblematicos.ui.juego.JuegoScreen
import com.example.ifts_android_consumosproblematicos.ui.juego.JuegoScreenViewModel
import com.example.ifts_android_consumosproblematicos.ui.menu.MenuScreen
import com.example.ifts_android_consumosproblematicos.ui.nombre.NombreScreen
import com.example.ifts_android_consumosproblematicos.ui.opciones.OpcionesScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Rutas {
    const val AVISO = "aviso"
    const val MENU = "menu"
    const val OPCIONES = "opciones"
    const val JUEGO = "juego/{modo}"
    const val NOMBRE = "nombre/{editar}"
    const val GAME_OVER = "game_over/{modo}/{rondas}/{nuevoRecord}"
    const val SUCCESS = "success/{estabilidad}/{presion}"
    const val RECUPERACION = "recuperacion"
    const val ARCADE_SETUP = "arcade_setup"
    const val ARCADE = "arcade"
    const val ARCADE_GAME_OVER = "arcade_game_over/{puntaje}/{comboMax}/{segundos}"
    const val RANKING = "ranking"

    fun juego(modo: ModoJuego) = "juego/${modo.name}"
    fun nombre(editar: Boolean) = "nombre/$editar"
    fun gameOver(modo: ModoJuego, rondas: Int = 0, nuevoRecord: Boolean = false) = "game_over/${modo.name}/$rondas/$nuevoRecord"
    fun success(estabilidad: Int, presion: Int) = "success/$estabilidad/$presion"
    fun arcadeGameOver(puntaje: Int, comboMax: Int, segundos: Int) = "arcade_game_over/$puntaje/$comboMax/$segundos"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.AVISO,
        enterTransition = { fadeIn(tween(350)) + slideInHorizontally(tween(350)) { it / 4 } },
        exitTransition = { fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 4 } },
        popEnterTransition = { fadeIn(tween(350)) + slideInHorizontally(tween(350)) { -it / 4 } },
        popExitTransition = { fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 4 } }
    ) {
        composable(Rutas.AVISO) {
            val scope = rememberCoroutineScope()
            AvisoInicialScreen(
                onContinuar = {
                    scope.launch {
                        val nombre = Repos.jugador.obtenerNombre()
                        if (nombre.isNullOrBlank()) {
                            navController.navigate(Rutas.nombre(editar = false)) {
                                popUpTo(Rutas.AVISO) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Rutas.MENU) {
                                popUpTo(Rutas.AVISO) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Rutas.MENU) { backStackEntry ->
            var nombreJugador by remember { mutableStateOf("") }

            val lifecycle = backStackEntry.lifecycle
            DisposableEffect(lifecycle) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        CoroutineScope(Dispatchers.Main).launch {
                            nombreJugador = Repos.jugador.obtenerNombre() ?: ""
                        }
                    }
                }
                lifecycle.addObserver(observer)
                onDispose {
                    lifecycle.removeObserver(observer)
                }
            }

            MenuScreen(
                nombre = nombreJugador,
                onIniciarPartida = { navController.navigate(Rutas.juego(ModoJuego.OLEADAS)) },
                onEndless = { navController.navigate(Rutas.juego(ModoJuego.ENDLESS)) },
                onOpciones = { navController.navigate(Rutas.OPCIONES) },
                onArcade = { navController.navigate(Rutas.ARCADE_SETUP) },
                onEditarNombre = { navController.navigate(Rutas.nombre(editar = true)) }
            )
        }

        composable(Rutas.OPCIONES) {
            OpcionesScreen(onVolver = { navController.popBackStack() })
        }

        composable(
            route = Rutas.NOMBRE,
            arguments = listOf(navArgument("editar") { type = NavType.BoolType; defaultValue = false })
        ) { entrada ->
            val editar = entrada.arguments?.getBoolean("editar") ?: false
            NombreScreen(
                editar = editar,
                onGuardar = {
                    if (editar) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Rutas.MENU) {
                            popUpTo(Rutas.NOMBRE) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = Rutas.JUEGO,
            arguments = listOf(navArgument("modo") { type = NavType.StringType })
        ) { entrada ->
            val modoString = entrada.arguments?.getString("modo") ?: ModoJuego.OLEADAS.name
            val modo = try {
                ModoJuego.valueOf(modoString)
            } catch (_: Exception) {
                ModoJuego.OLEADAS
            }
            val viewModel: JuegoScreenViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val recuperacionGanoFlow = entrada.savedStateHandle.getStateFlow<Boolean?>("recuperacion_gano", null)
            val recuperacionGano by recuperacionGanoFlow.collectAsStateWithLifecycle()

            LaunchedEffect(recuperacionGano) {
                recuperacionGano?.let { gano ->
                    entrada.savedStateHandle.remove<Boolean>("recuperacion_gano")
                    viewModel.resolverRecuperacion(gano)
                }
            }

            LaunchedEffect(uiState.necesitaRecuperacion) {
                if (uiState.necesitaRecuperacion) {
                    viewModel.notificarRecuperacionIniciada()
                    navController.navigate(Rutas.RECUPERACION)
                }
            }

            val scope = rememberCoroutineScope()
            LaunchedEffect(uiState.juegoTerminado) {
                if (uiState.juegoTerminado) {
                    if (uiState.esVictoria) {
                        navController.navigate(Rutas.success(uiState.estabilidad, uiState.presion)) {
                            popUpTo(Rutas.JUEGO) { inclusive = true }
                        }
                    } else {
                        if (modo == ModoJuego.ENDLESS) {
                            val rondasSup = uiState.rondaActual - 1
                            scope.launch {
                                val nuevoRec = Repos.prefs.registrarEndless(rondasSup)
                                navController.navigate(Rutas.gameOver(modo, rondas = rondasSup, nuevoRecord = nuevoRec)) {
                                    popUpTo(Rutas.JUEGO) { inclusive = true }
                                }
                            }
                        } else {
                            navController.navigate(Rutas.gameOver(modo, rondas = 0, nuevoRecord = false)) {
                                popUpTo(Rutas.JUEGO) { inclusive = true }
                            }
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
                onSalir = { navController.popBackStack() },
                onPausar = { viewModel.pausarTemporizador() },
                onReanudar = { viewModel.reanudarTemporizador() },
                segundaVidaDisponible = uiState.segundaVidaDisponible
            )
        }

        composable(
            route = Rutas.GAME_OVER,
            arguments = listOf(
                navArgument("modo") { type = NavType.StringType; defaultValue = ModoJuego.OLEADAS.name },
                navArgument("rondas") { type = NavType.IntType; defaultValue = 0 },
                navArgument("nuevoRecord") { type = NavType.BoolType; defaultValue = false }
            )
        ) { entrada ->
            val modoString = entrada.arguments?.getString("modo") ?: ModoJuego.OLEADAS.name
            val modo = try {
                ModoJuego.valueOf(modoString)
            } catch (_: Exception) {
                ModoJuego.OLEADAS
            }
            val rondas = entrada.arguments?.getInt("rondas") ?: 0
            val nuevoRecord = entrada.arguments?.getBoolean("nuevoRecord") ?: false

            GameOverScreen(
                modo = modo,
                rondasSuperadas = rondas,
                nuevoRecord = nuevoRecord,
                onReintentar = {
                    navController.navigate(Rutas.juego(modo)) {
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

        composable(Rutas.RECUPERACION) {
            RecuperacionScreen(
                navController = navController
            )
        }

        composable(Rutas.ARCADE_SETUP) {
            ArcadeSetupScreen(
                onIniciarArcade = {
                    navController.navigate(Rutas.ARCADE) {
                        popUpTo(Rutas.ARCADE_SETUP) { inclusive = true }
                    }
                },
                onVerRanking = { navController.navigate(Rutas.RANKING) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.ARCADE) {
            ArcadeScreen(
                onTermino = { resultado ->
                    navController.navigate(
                        Rutas.arcadeGameOver(resultado.puntaje, resultado.comboMax, resultado.segundos)
                    ) {
                        popUpTo(Rutas.ARCADE) { inclusive = true }
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
            route = Rutas.ARCADE_GAME_OVER,
            arguments = listOf(
                navArgument("puntaje") { type = NavType.IntType; defaultValue = 0 },
                navArgument("comboMax") { type = NavType.IntType; defaultValue = 0 },
                navArgument("segundos") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { entrada ->
            val puntaje = entrada.arguments?.getInt("puntaje") ?: 0
            val comboMax = entrada.arguments?.getInt("comboMax") ?: 0
            val segundos = entrada.arguments?.getInt("segundos") ?: 0

            ArcadeGameOverScreen(
                puntaje = puntaje,
                comboMax = comboMax,
                segundos = segundos,
                onReintentar = {
                    navController.navigate(Rutas.ARCADE) {
                        popUpTo(Rutas.MENU)
                    }
                },
                onVerRanking = { navController.navigate(Rutas.RANKING) },
                onVolverMenu = {
                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.MENU) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.RANKING) {
            RankingScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
