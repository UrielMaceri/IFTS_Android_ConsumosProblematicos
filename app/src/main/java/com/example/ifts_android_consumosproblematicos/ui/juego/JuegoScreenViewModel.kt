package com.example.ifts_android_consumosproblematicos.ui.juego

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.ifts_android_consumosproblematicos.data.SituacionesRepository
import com.example.ifts_android_consumosproblematicos.model.ModoJuego
import com.example.ifts_android_consumosproblematicos.model.Opcion
import com.example.ifts_android_consumosproblematicos.model.Situacion
import com.example.ifts_android_consumosproblematicos.model.TipoOpcion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class JuegoUiState(
    val situacionActual: Situacion? = null,
    val estabilidad: Int = 100,
    val presion: Int = 50,
    val rondaActual: Int = 1,
    val totalRondas: Int = 10,
    val tiempoRestanteSeg: Int = 25,
    val juegoTerminado: Boolean = false,
    val esVictoria: Boolean = false,
    val necesitaRecuperacion: Boolean = false,
    val segundaVidaDisponible: Boolean = true
)

class JuegoScreenViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val repo = SituacionesRepository(getApplication())

    val modo: ModoJuego = try {
        val modoString = savedStateHandle.get<String>("modo")
        if (modoString != null) ModoJuego.valueOf(modoString) else ModoJuego.OLEADAS
    } catch (_: Exception) {
        ModoJuego.OLEADAS
    }

    private val _uiState = MutableStateFlow(JuegoUiState())
    val uiState: StateFlow<JuegoUiState> = _uiState.asStateFlow()

    private var colaSituaciones: MutableList<Situacion> = mutableListOf()
    private var ultimaSituacionId: Int? = null
    private var temporizadorJob: Job? = null

    init {
        iniciarPartida()
    }

    fun iniciarPartida() {
        temporizadorJob?.cancel()
        colaSituaciones.clear()
        ultimaSituacionId = null

        val total = if (modo == ModoJuego.ENDLESS) Int.MAX_VALUE else 10
        val primeraSituacion = obtenerSiguienteSituacion()

        _uiState.value = JuegoUiState(
            situacionActual = primeraSituacion,
            estabilidad = 100,
            presion = 50,
            rondaActual = 1,
            totalRondas = total,
            tiempoRestanteSeg = 25,
            juegoTerminado = false,
            esVictoria = false
        )

        iniciarTemporizador()
    }

    private fun obtenerSiguienteSituacion(): Situacion? {
        if (colaSituaciones.isEmpty()) {
            val todas = repo.obtenerTodas()
            val disponibles = if (ultimaSituacionId != null && todas.size > 1) {
                todas.filter { it.id != ultimaSituacionId }
            } else {
                todas
            }
            colaSituaciones = disponibles.shuffled().toMutableList()
            if (colaSituaciones.isEmpty()) {
                colaSituaciones = todas.shuffled().toMutableList()
            }
        }
        val sig = colaSituaciones.removeAt(0)
        ultimaSituacionId = sig.id
        return sig
    }

    fun seleccionarOpcion(opcion: Opcion) {
        if (_uiState.value.juegoTerminado) return

        temporizadorJob?.cancel()

        val estadoActual = _uiState.value
        var nuevaEstabilidad = estadoActual.estabilidad
        var nuevaPresion = estadoActual.presion

        val porcentajeEstabilidad = Random.nextInt(10, 26)  // Entre 10% y 25%
        val decrementoPresion = 25                                        // La presión baja siempre un 25%

        when (opcion.tipo) {
            TipoOpcion.Positiva -> {
                // Opción Buena: baja presión 25% y sube estabilidad (10% a 25%)
                nuevaPresion = (nuevaPresion - decrementoPresion).coerceAtLeast(0)
                nuevaEstabilidad = (nuevaEstabilidad + porcentajeEstabilidad).coerceAtMost(100)
            }
            TipoOpcion.Neutral -> {
                // Opción Neutral: solo baja presión 25%
                nuevaPresion = (nuevaPresion - decrementoPresion).coerceAtLeast(0)
            }
            TipoOpcion.Negativa -> {
                // Opción Mala: siempre baja estabilidad (10% a 25%)
                nuevaEstabilidad = (nuevaEstabilidad - porcentajeEstabilidad).coerceAtLeast(0)
                // Tiene 50% de probabilidad de bajar presión en 25%
                val bajaPresion = Random.nextBoolean()
                if (bajaPresion) {
                    nuevaPresion = (nuevaPresion - decrementoPresion).coerceAtLeast(0)
                }
            }
        }

        avanzarRonda(nuevaEstabilidad, nuevaPresion)
    }

    private fun tiempoAgotado() {
        if (_uiState.value.juegoTerminado) return

        val estadoActual = _uiState.value
        var nuevaEstabilidad = estadoActual.estabilidad
        var nuevaPresion = estadoActual.presion

        if (nuevaPresion < 100) {
            // Si la presión es menor a 100%, dejar pasar el tiempo sube la presión social
            nuevaPresion = (nuevaPresion + 25).coerceAtMost(100)
        } else {
            // Si la presión social ya llegó al 100%, no contestar baja la estabilidad a un paso mayor (entre 25% y 35%)
            val castigoEstabilidad = Random.nextInt(25, 36)
            nuevaEstabilidad = (nuevaEstabilidad - castigoEstabilidad).coerceAtLeast(0)
        }

        avanzarRonda(nuevaEstabilidad, nuevaPresion)
    }

    private fun avanzarRonda(nuevaEstabilidad: Int, nuevaPresion: Int) {
        if (nuevaEstabilidad <= 0) {
            // Derrota por falta de estabilidad
            _uiState.update {
                it.copy(
                    estabilidad = 0,
                    presion = nuevaPresion,
                    juegoTerminado = true,
                    esVictoria = false
                )
            }
            return
        }

        val siguienteRonda = _uiState.value.rondaActual + 1

        if (modo == ModoJuego.OLEADAS && siguienteRonda > _uiState.value.totalRondas) {
            // Victoria al completar todas las rondas
            _uiState.update {
                it.copy(
                    estabilidad = nuevaEstabilidad,
                    presion = nuevaPresion,
                    juegoTerminado = true,
                    esVictoria = true
                )
            }
            return
        }

        // Siguiente ronda normal
        val siguienteSituacion = obtenerSiguienteSituacion()
        val tiempoNuevo = if (modo == ModoJuego.ENDLESS) {
            maxOf(10, 25 - (siguienteRonda - 1) / 5)
        } else {
            25
        }

        _uiState.update {
            it.copy(
                situacionActual = siguienteSituacion,
                estabilidad = nuevaEstabilidad,
                presion = nuevaPresion,
                rondaActual = siguienteRonda,
                tiempoRestanteSeg = tiempoNuevo
            )
        }

        iniciarTemporizador()
    }

    private fun iniciarTemporizador() {
        temporizadorJob?.cancel()
        temporizadorJob = viewModelScope.launch {
            while (_uiState.value.tiempoRestanteSeg > 0) {
                delay(1000L)
                _uiState.update { it.copy(tiempoRestanteSeg = it.tiempoRestanteSeg - 1) }
            }
            tiempoAgotado()
        }
    }

    fun pausarTemporizador() {
        temporizadorJob?.cancel()
    }

    fun reanudarTemporizador() {
        if (!_uiState.value.juegoTerminado && _uiState.value.tiempoRestanteSeg > 0) {
            if (temporizadorJob?.isActive != true) {
                iniciarTemporizador()
            }
        }
    }

    override fun onCleared() {
        temporizadorJob?.cancel()
    }
}
