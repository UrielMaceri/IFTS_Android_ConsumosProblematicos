# SDD: Minijuego del corazón + nombre de jugador + ranking global

Documento de **Spec-Driven Development** para trabajar con agentes de código (Claude Code, Copilot, etc.).
Contiene **solo lo que falta hacer**, separado por desarrollador: **Uriel**, **Lucas** y **Matias**.

Referencia jugable del comportamiento del minijuego: https://claude.ai/artifact/YQUHsCRyozN81geNngis2P

---

## 0. Cómo usar este documento con un agente

Cada tarea es una **spec** autocontenida. Para delegarla a un agente, pasarle:

1. La sección **1** (contexto del código) y la **2** (decisiones cerradas).
2. La sección **3** (contratos compartidos).
3. **Una sola spec** (por ejemplo `SPEC-L2`).

Plantilla de prompt:

```
Implementá SPEC-XX del documento adjunto.
- Tocá únicamente los archivos listados en "Archivos".
- No agregues dependencias que no estén listadas en la spec.
- Código y nombres en español, igual que el resto del proyecto.
- Strings de UI en strings.xml con prefijo corazon_ (excepto lo que ya esté hardcodeado en la pantalla que modificás).
- Al terminar, ejecutá los comandos de "Verificación" y mostrame el resultado.
- Si algo de la spec es ambiguo o contradice el código, frená y preguntá antes de inventar.
```

**Reglas para todos los agentes**

- Compose únicamente. No crear XML de pantallas ni Views.
- Nada de píxeles fijos en el minijuego: todo en fracción de la altura del `Canvas`.
- Colores: usar la paleta de `ui/theme/Color.kt`; colores nuevos van en `ui/theme/ColorCorazon.kt`.
- Cada spec define sus **criterios de aceptación** y su **verificación**. Una spec no está terminada hasta que ambos se cumplan.
- No modificar archivos de otra spec. Si hace falta, avisar al dueño.

---

## 1. Contexto del código (línea base)

Paquete base: `com.example.ifts_android_consumosproblematicos` (en `app/src/main/java/...`).

| Elemento | Estado actual |
|---|---|
| Stack | Kotlin, Jetpack Compose, Material 3, MVVM, Navigation Compose, `kotlinx-serialization`, `datastore-preferences` (sin uso aún). `minSdk 24`, JUnit 4 para tests |
| Pantalla de juego | `ui/juego/JuegoScreen.kt` + `JuegoScreenViewModel.kt` (`StateFlow<JuegoUiState>`) |
| Navegación | `ui/navigation/AppNavigation.kt` (`object Rutas` + `NavHost`). El `viewModel()` del juego está atado a la ruta `juego/{modo}` |
| Modos | `enum class ModoJuego { OLEADAS, ENDLESS }`. **OLEADAS = «Iniciar Partida»** (10 rondas) |
| Punto de derrota | `avanzarRonda()` en el ViewModel: `if (nuevaEstabilidad <= 0)` marca `juegoTerminado = true` |
| Modo en el ViewModel | **No lo conoce**: siempre arma 10 rondas. `modo` solo llega a `JuegoScreen` |
| Endless | **No implementado**: en `MenuScreen` el parámetro `onEndless` existe pero el botón no se dibuja; el ViewModel no tiene lógica infinita |
| Menú | `MenuScreen` muestra solo «Iniciar Partida». El botón «Opciones» está comentado |
| Opciones | `OpcionesScreen` con switches de música y vibración usando `rememberSaveable` (no persisten) |
| Game Over | `GameOverScreen(onReintentar, onVolverMenu)`: «Reintentar» navega **siempre** a `OLEADAS` (bug) |
| Manifest | Sin `INTERNET`. Orientación sin bloquear |
| Backend | Ninguno (sin Firebase) |
| Valores del juego principal | Estabilidad inicial 100, Presión inicial 50, 25 s por ronda, la Estabilidad cambia ±10–25 al azar por opción |

---

## 2. Decisiones cerradas

| Tema | Decisión |
|---|---|
| Orientación | **Toda la app bloqueada en vertical** (`android:screenOrientation="portrait"`) |
| Segunda vida: cuándo | Cuando la Estabilidad del juego principal llega a 0 (por opción o por tiempo agotado), si está disponible |
| Segunda vida: al ganar | **Misma ronda, misma situación**, tiempo reiniciado a 25 s, **Estabilidad 100%**, Presión sin cambios |
| Segunda vida: al perder (o apretar Atrás) | Game Over normal |
| Segunda vida en OLEADAS | 1 vez por partida |
| Segunda vida en ENDLESS | Booleano `disponible`. Se consume al usarla. Se **recarga al completar una ronda múltiplo de 10** (10, 20, 30…). **No se acumula** |
| Indicador de segunda vida | Obligatorio y visible en el HUD del juego principal (OLEADAS y ENDLESS) mientras está disponible |
| Segunda vida: inicio | Estabilidad en 0%. **El primer rojo puede hacerte perder**: no se regala vida |
| Mecánica | 3 carriles, rojo −25, verde +25, esquivar +1 combo. Estabilidad solo en pasos de 25 |
| Escudo emocional | Solo modo arcade. On/off, se activa en combos múltiplos de 5, absorbe el primer rojo y el combo igual se reinicia |
| Controles del corazón | Dos modos: **táctil** (tocar mitad izquierda/derecha **y** deslizar) o **inclinación** (acelerómetro). Es una preferencia persistente |
| Dónde se elige el control | En **Opciones** (accesible desde el menú principal) |
| Nombre de jugador | Pantalla «introducí tu nombre» **la primera vez**. Se guarda en **SQLite local**. Editable desde el menú principal. Es el nombre del ranking |
| Preferencias y récords | DataStore (color del corazón, modo de control, mejor puntaje, mejor combo, récord Endless) |
| Ranking global | **Firebase Firestore + Authentication anónima**. **Un documento por jugador** (su mejor puntaje) |
| Privacidad | El ranking guarda **solo nombre, puntaje y combo**. Se pide un apodo, no el nombre real |
| Inyección de dependencias | Sin librerías: un `object Repos` simple (ver sección 3) |

---

## 3. Contratos compartidos

Los publica **Uriel en SPEC-U0**. Todas las demás specs se apoyan en estos tipos y no los modifican.

```kotlin
// model/ConfigCorazon.kt
enum class ModoControl { TACTIL, INCLINACION }

data class ConfigCorazon(
    val estabilidadInicial: Int,
    val ganaEn: Int? = null,             // 100 en recuperación, null en arcade
    val escudoActivo: Boolean,
    val puntajeActivo: Boolean,
    val probVerde: Float = 0.18f,
    val velocidadMax: Float = 0.94f,     // alturas de pantalla por segundo
    val primeraFilaVerde: Boolean = false,
) {
    companion object {
        val ARCADE = ConfigCorazon(
            estabilidadInicial = 100, escudoActivo = true, puntajeActivo = true)
        val RECUPERACION = ConfigCorazon(
            estabilidadInicial = 0, ganaEn = 100, escudoActivo = false,
            puntajeActivo = false, probVerde = 0.5f, velocidadMax = 0.65f,
            primeraFilaVerde = true)
    }
}

// model/EstadoHud.kt  (el motor lo publica solo cuando cambia algo)
data class EstadoHud(
    val estabilidad: Int,
    val combo: Int,
    val puntaje: Int,
    val multiplicador: Int,
    val escudo: Boolean,
    val progresoEscudo: Int,     // combo % 5 (0..4)
)

// model/EventoCorazon.kt  (eventos puntuales: sonido, vibración, textos flotantes)
sealed interface EventoCorazon {
    data object EscudoActivado : EventoCorazon
    data object EscudoRoto : EventoCorazon
    data object Golpe : EventoCorazon
    data object Recolecto : EventoCorazon
    data class Esquivo(val combo: Int) : EventoCorazon
}

// model/ResultadoCorazon.kt
data class ResultadoCorazon(val gano: Boolean, val puntaje: Int, val comboMax: Int, val segundos: Int)

// util/SegundaVida.kt   (la implementa SPEC-U3)
class SegundaVida(private val recargaCadaRondas: Int? = null) {
    var disponible = true
        private set
    fun consumir()                    // check(disponible)
    fun alCompletarRonda(ronda: Int)  // si recargaCadaRondas != null && ronda % recargaCadaRondas == 0 -> disponible = true
}

// data/JugadorRepository.kt   (SQLite; la implementa SPEC-L1)
interface JugadorRepository {
    suspend fun obtenerNombre(): String?
    suspend fun guardarNombre(nombre: String)
}

// data/PreferenciasRepo.kt   (DataStore; la implementa SPEC-L2)
interface PreferenciasRepo {
    val colorCorazon: Flow<Int>          // ARGB, por defecto 0xFFFF4D79
    val modoControl: Flow<ModoControl>   // por defecto TACTIL
    val mejorPuntaje: Flow<Int>
    val mejorCombo: Flow<Int>
    val recordEndless: Flow<Int>
    suspend fun setColorCorazon(argb: Int)
    suspend fun setModoControl(modo: ModoControl)
    suspend fun registrarArcade(puntaje: Int, combo: Int)
    suspend fun registrarEndless(rondasSuperadas: Int): Boolean  // true si es récord nuevo
}

// data/ranking/RankingRepository.kt   (Fake: SPEC-U0, Firestore: SPEC-U4)
data class EntradaRanking(val uid: String, val nombre: String, val puntaje: Int, val comboMax: Int)

interface RankingRepository {
    suspend fun miUid(): String?
    suspend fun enviar(nombre: String, puntaje: Int, comboMax: Int): Result<Unit>
    suspend fun top(limite: Int = 20): Result<List<EntradaRanking>>
}

// data/Repos.kt   (U0 lo crea con implementaciones falsas; cada dueño cambia SU línea)
object Repos {
    lateinit var jugador: JugadorRepository
    lateinit var prefs: PreferenciasRepo
    lateinit var ranking: RankingRepository
    fun init(context: Context) { /* asignar implementaciones; se llama desde MainActivity */ }
}
```

**Rutas** (las define U0; nadie más edita el `object Rutas`):

```kotlin
object Rutas {
    const val AVISO = "aviso"
    const val MENU = "menu"
    const val OPCIONES = "opciones"
    const val JUEGO = "juego/{modo}"
    const val NOMBRE = "nombre/{editar}"                       // fun nombre(editar: Boolean)
    const val GAME_OVER = "game_over/{modo}/{rondas}/{nuevoRecord}"  // fun gameOver(modo, rondas, nuevoRecord)
    const val SUCCESS = "success/{estabilidad}/{presion}"      // sin cambios
    const val RECUPERACION = "recuperacion"
    const val ARCADE_SETUP = "arcade_setup"
    const val ARCADE = "arcade"
    const val ARCADE_GAME_OVER = "arcade_game_over/{puntaje}/{comboMax}/{segundos}"
    const val RANKING = "ranking"
}
```

---

## 4. Especificación funcional del minijuego (referencia para U1, U2 y U3)

**Mundo:** coordenadas normalizadas. `y = 0` arriba, `y = 1` abajo. Carriles `0, 1, 2`. Posición del corazón: `y = 0,81`.

| Parámetro | Valor |
|---|---|
| Velocidad | `min(velocidadMax, 0,36 + 0,00625 × segundos)` alturas/s |
| Nivel | `floor(segundos / 12)` |
| Separación entre filas | `max(0,30; 0,47 − 0,01875 × nivel)` alturas |
| Filas | Cada fila deja **un carril libre**. Los otros 2 carriles: 1 rojo, o 2 rojos con prob. `0,2 + min(0,45; 0,05 × nivel)` |
| Verde | En el carril libre, con prob. `probVerde` (mínimo 0,40 si `estabilidad ≤ 50`) |
| Primera fila | Si `primeraFilaVerde`: exactamente **1 verde y 0 rojos** |
| Colisión | `abs(posCarril − carrilObstaculo) < 0,46` y `abs(y − 0,81) < 0,0625` |
| Esquive | Un rojo no tocado con `y > 0,88` |
| Movimiento | `posCarril` se acerca al carril objetivo: `pos += (obj − pos) × min(1, dt × 18)` |

**Eventos**

| Evento | Efecto |
|---|---|
| Toca un verde | `estabilidad = min(100, +25)`, `bump()`, `puntaje += 25 × mult` (si hay puntaje) |
| Esquiva un rojo | `bump()`, `puntaje += 10 × mult` (si hay puntaje) |
| Toca un rojo con escudo | Escudo se rompe, estabilidad no cambia, combo = 0 |
| Toca un rojo sin escudo | `estabilidad −25`, combo = 0. Si llega a 0: **pierde** |
| `bump()` | `combo++`, actualiza `comboMax`. Si `escudoActivo` y `combo % 5 == 0` y no hay escudo: se activa el escudo |
| Multiplicador | `1 + combo / 5` (entera), calculado **después** de `bump()` |
| Victoria | Si `ganaEn != null` y `estabilidad >= ganaEn` |

---

## 5. Specs de Uriel

### SPEC-U0: Esqueleto y contratos (Fase 0, bloquea al resto)

**Objetivo:** dejar el proyecto listo para trabajar en paralelo sin conflictos.

**Archivos**
- Crear: todos los de la sección 3; `data/Repos.kt`; `FakeJugadorRepository`, `FakePreferenciasRepo` y `FakeRankingRepository` (en memoria); una pantalla vacía («Próximamente») por cada ruta nueva, **cada una en su archivo** (`ui/corazon/RecuperacionScreen.kt`, `ArcadeSetupScreen.kt`, `ArcadeScreen.kt`, `ArcadeGameOverScreen.kt`, `RankingScreen.kt`, `ui/nombre/NombreScreen.kt`).
- Modificar: `AppNavigation.kt`, `MainActivity.kt`, `AndroidManifest.xml`, `JuegoScreenViewModel.kt`, `JuegoScreen.kt`, `MenuScreen.kt`, `GameOverScreen.kt`.

**Requisitos**
1. El manifest bloquea `MainActivity` en `portrait` y declara `INTERNET`.
2. `MainActivity` llama a `Repos.init(applicationContext)` antes de `setContent`.
3. `Rutas` queda exactamente como en la sección 3 (con funciones `nombre()`, `gameOver()`, etc.). Todas las rutas nuevas están registradas en el `NavHost` con su pantalla vacía.
4. Las llamadas existentes a `Rutas.GAME_OVER` pasan a `Rutas.gameOver(modo, rondas = 0, nuevoRecord = false)`. `GameOverScreen` recibe `modo: ModoJuego`, `rondasSuperadas: Int`, `nuevoRecord: Boolean` (con valores por defecto) y su «Reintentar» navega a `Rutas.juego(modo)`.
5. `JuegoScreenViewModel` recibe `SavedStateHandle` y lee `modo`. Sin cambios de comportamiento todavía.
6. `JuegoUiState` agrega `necesitaRecuperacion: Boolean = false` y `segundaVidaDisponible: Boolean = true`.
7. `JuegoScreen` agrega el parámetro `segundaVidaDisponible: Boolean = false` (sin UI todavía) y `AppNavigation` se lo pasa desde `uiState`.
8. `MenuScreen` agrega parámetros con valor por defecto: `nombre: String = ""`, `onArcade: (() -> Unit)? = null`, `onEditarNombre: () -> Unit = {}`. Sin UI nueva todavía.
9. El `composable` de `AVISO` y los `composable` de cada ruta nueva quedan en **bloques separados** del `NavHost`, para que cada dueño edite solo el suyo.

**Criterios de aceptación**
- Dado el proyecto compilado, cuando se navega a cada ruta nueva, entonces se muestra «Próximamente» sin crashear.
- Dado un juego de OLEADAS, cuando se pierde, entonces sigue llegando a Game Over y «Reintentar» vuelve a OLEADAS.
- La app no rota al girar el teléfono.

**Verificación:** `./gradlew assembleDebug` y `./gradlew testDebugUnitTest`.

---

### SPEC-U1: `MotorCorazon` (lógica pura)

**Objetivo:** motor del minijuego sin dependencias de Android, 100% testeable.

**Archivos:** crear `ui/corazon/MotorCorazon.kt` y `app/src/test/.../MotorCorazonTest.kt`.

**Interfaz**
```kotlin
class MotorCorazon(val config: ConfigCorazon, private val azar: Random = Random.Default) {
    val hud: StateFlow<EstadoHud>
    val eventos: SharedFlow<EventoCorazon>      // extraBufferCapacity >= 8
    val posCarril: Float                         // 0f..2f, para dibujar
    val obstaculos: List<ObstaculoCorazon>       // solo lectura para el dibujo
    var resultado: ResultadoCorazon?             // null mientras se juega
        private set

    fun mover(delta: Int)                        // -1 / +1, limitado a 0..2
    fun irACarril(carril: Int)                   // para el control por inclinación
    fun actualizar(dt: Float)                    // no hace nada si ya terminó
    internal fun agregarObstaculoParaTest(carril: Int, y: Float, bueno: Boolean)
}

data class ObstaculoCorazon(val carril: Int, var y: Float, val bueno: Boolean, val icono: Int)
```

**Requisitos:** implementar la sección 4 completa. `icono` es un índice al azar para que la UI elija qué imagen dibujar. `actualizar` limita `dt` a 0,05.

**Criterios de aceptación** (cada uno con un test)
- Dado `ARCADE`, cuando `estabilidad = 100` y un rojo toca al corazón, entonces `estabilidad = 75`, `combo = 0`.
- Dado `ARCADE` con 5 esquives seguidos, entonces `escudo = true` y se emite `EscudoActivado`; con 10 esquives y escudo ya activo, sigue en `true` (no se acumula).
- Dado el escudo activo y un rojo que toca, entonces `escudo = false`, `estabilidad` sin cambios, `combo = 0` y se emite `EscudoRoto`.
- Dado `ARCADE` con estabilidad 25 y un rojo sin escudo, entonces `resultado.gano == false`.
- Dado `RECUPERACION` (empieza en 0), cuando se agarran 4 verdes seguidos, entonces `resultado.gano == true`.
- Dado `RECUPERACION`, cuando un rojo toca con estabilidad 0, entonces `resultado.gano == false`.
- Dado `RECUPERACION`, la primera fila generada tiene exactamente 1 verde y 0 rojos.
- Dado cualquier fila generada, siempre hay al menos un carril sin rojo.
- Puntaje: esquive con combo 5 suma `10 × 2`; verde con combo 0 suma `25 × 1`.
- La estabilidad nunca supera 100 ni baja de 0, y solo toma valores múltiplos de 25.

**Verificación:** `./gradlew testDebugUnitTest --tests "*MotorCorazonTest"`.
**Fuera de alcance:** dibujo, sonido, sensores.

---

### SPEC-U2: `CorazonJuego` (Canvas, game loop, input táctil)

**Objetivo:** composable que muestra y controla un `MotorCorazon`.

**Archivos:** crear `ui/corazon/CorazonJuego.kt`. **Depende de:** U1.

**Interfaz**
```kotlin
@Composable
fun CorazonJuego(
    motor: MotorCorazon,
    colorCorazon: Color,
    modoControl: ModoControl,
    pausado: Boolean = false,
    onTermino: (ResultadoCorazon) -> Unit,
    modifier: Modifier = Modifier,
)
```

**Requisitos**
1. Game loop con `LaunchedEffect` + `withFrameNanos`. Al aparecer `motor.resultado`, llama a `onTermino` **una sola vez**.
2. El loop se frena si `pausado` o si la app pasa a segundo plano (`LifecycleEventEffect`), y retoma sin saltos de tiempo.
3. Dibuja: fondo con líneas de carril que se desplazan, obstáculos (rojo/verde, ver abajo), corazón con `Path` teñido con `colorCorazon`, anillo del escudo cuando `hud.escudo` es `true`.
4. Obstáculos: por ahora un rectángulo redondeado rojo o verde con un **símbolo distinto** (por ejemplo «!» para malo y «+» para bueno) para que no dependa solo del color. Cuando Matias entregue los íconos (M2) se cambian por los vectores.
5. Si `modoControl == TACTIL`: tocar la mitad izquierda/derecha llama a `motor.mover(-1/+1)` **y** un deslizamiento horizontal también mueve.
6. Si `modoControl == INCLINACION`: no procesa toques para mover y usa `EfectoInclinacion(activo = true, onCarril = motor::irACarril)` (de SPEC-L3). Mientras L3 no esté, dejar el llamado detrás de un `TODO` sin romper el build.
7. Suscribirse a `motor.eventos` y emitir un efecto visual breve (partículas o destello) en `Golpe` y `Recolecto`. La vibración se usará más adelante, no ahora.
8. Sin objetos nuevos por frame cuando sea evitable.

**Criterios de aceptación**
- Dado el juego corriendo en un emulador, el corazón se mueve de carril al tocar cada mitad y al deslizar.
- Dado un `pausado = true`, nada se mueve; al volver a `false` no hay salto.
- Dado el teléfono en otro tamaño de pantalla, las proporciones se mantienen.
- `onTermino` se llama exactamente una vez.

**Verificación:** `./gradlew assembleDebug` + prueba manual en emulador (describir en el PR qué se probó).

---

### SPEC-U3: `SegundaVida` + integración con el juego principal

**Objetivo:** disparar el minijuego al perder y reanudar la partida.

**Archivos:** crear `util/SegundaVida.kt` y `RecuperacionScreen.kt` (reemplaza el stub), modificar `JuegoScreenViewModel.kt` y el bloque `JUEGO` y `RECUPERACION` de `AppNavigation.kt`, crear `SegundaVidaTest.kt`. **Depende de:** U0, U1, U2. **Merge después de M5.**

**Requisitos**
1. `SegundaVida` según la sección 3. En el ViewModel: `SegundaVida(null)` para OLEADAS y `SegundaVida(10)` para ENDLESS. Se crea de nuevo en cada `iniciarPartida()`.
2. En `avanzarRonda()`, si `nuevaEstabilidad <= 0` y `segundaVida.disponible`: `consumir()`, el estado queda con `estabilidad = 0`, `presion = nuevaPresion`, `necesitaRecuperacion = true`, `juegoTerminado = false` y el temporizador **no** se reinicia. Si no está disponible: comportamiento actual.
3. Cuando se **completa** una ronda (la partida avanza a la siguiente), llamar `segundaVida.alCompletarRonda(rondaActual)` y reflejar `segundaVidaDisponible` en `uiState`.
4. `resolverRecuperacion(gano: Boolean)`:
   - `true`: `estabilidad = 100`, misma `situacionActual` y `rondaActual`, `tiempoRestanteSeg = 25`, `necesitaRecuperacion = false`, presión sin cambios, reinicia el temporizador.
   - `false`: `juegoTerminado = true`, `esVictoria = false`.
5. `AppNavigation` (bloque `JUEGO`): cuando `necesitaRecuperacion` es `true`, navega a `Rutas.RECUPERACION` **sin `popUpTo`** (el ViewModel tiene que sobrevivir). Recibe el resultado por `savedStateHandle["recuperacion_gano"]` y llama a `resolverRecuperacion`, luego limpia la clave.
6. `RecuperacionScreen`: muestra `RecuperacionIntro` (de M4) y luego `CorazonJuego` con `ConfigCorazon.RECUPERACION`, `HudRecuperacion` (de M2), el color y el modo de control de `Repos.prefs`. Al terminar, escribe el resultado en `previousBackStackEntry.savedStateHandle` y hace `popBackStack()`. **`BackHandler`: Atrás cuenta como perder.**

**Tabla de comportamiento de `SegundaVida(10)`** (tests en `SegundaVidaTest`)

| Escenario | Esperado |
|---|---|
| Partida nueva | `disponible = true` |
| Se usa en la ronda 1; se completan rondas 2 a 9 | sigue `false` |
| Se usa en la ronda 1; se completa la ronda 10 | `true` |
| No se usa en las rondas 1 a 10 | sigue `true` (una sola) |
| No se usa en las rondas 1 a 20 | sigue `true` (una sola, no acumula) |
| No se usa hasta la ronda 21 y se usa ahí; se completan 22 a 29 | `false` |
| Idem; se completa la ronda 30 | `true` |
| `consumir()` dos veces seguidas | lanza error |
| `SegundaVida(null)`, `alCompletarRonda(10)` | nunca recarga |

**Criterios de aceptación**
- Dado OLEADAS, cuando se pierde por primera vez, entonces aparece la intro y el minijuego; al ganar, la partida sigue en la misma ronda con Estabilidad 100%.
- Dado OLEADAS, cuando se pierde por segunda vez en la misma partida, entonces Game Over directo.
- Dado el minijuego, cuando se pierde o se aprieta Atrás, entonces Game Over.
- Dado Endless, la segunda vida vuelve a estar disponible según la tabla.
- Mandar la app a segundo plano durante el minijuego no pierde la partida.

**Verificación:** `./gradlew testDebugUnitTest --tests "*SegundaVidaTest"` y `./gradlew assembleDebug`.

---

### SPEC-U4: Ranking global con Firebase

**Objetivo:** leaderboard compartido entre dispositivos. **Depende de:** U0.

**Parte A: pasos manuales de Uriel (no se delegan a un agente)**
1. Entrar a https://console.firebase.google.com y crear un proyecto (plan gratuito Spark).
2. «Agregar app» → Android → package name `com.example.ifts_android_consumosproblematicos`.
3. Descargar `google-services.json` y copiarlo en `app/`.
4. En **Authentication → Sign-in method**, activar el proveedor **Anónimo**.
5. En **Firestore Database → Crear base de datos** (modo producción, región cercana).
6. En **Firestore → Reglas**, pegar las de abajo y publicarlas.
7. Probar las reglas con el simulador de la consola antes de usarlas.

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /ranking/{uid} {
      allow read: if true;
      allow create, update: if request.auth != null
        && request.auth.uid == uid
        && request.resource.data.keys().hasOnly(['nombre','puntaje','comboMax','actualizado'])
        && request.resource.data.nombre is string
        && request.resource.data.nombre.size() >= 2
        && request.resource.data.nombre.size() <= 12
        && request.resource.data.puntaje is int
        && request.resource.data.puntaje >= 0
        && request.resource.data.puntaje <= 100000
        && request.resource.data.comboMax is int
        && request.resource.data.comboMax >= 0
        && request.resource.data.actualizado == request.time
        && (resource == null || request.resource.data.puntaje >= resource.data.puntaje);
      allow delete: if false;
    }
  }
}
```

El repo es público: `google-services.json` no es secreto, pero **las reglas son la única protección**, así que no se publican reglas abiertas.

**Parte B: código (agente)**

**Archivos:** modificar `app/build.gradle.kts`, `gradle/libs.versions.toml`, `build.gradle.kts`; crear `data/ranking/FirestoreRankingRepository.kt`; modificar la línea de `ranking` en `Repos.kt`.

**Requisitos**
1. Agregar el plugin `com.google.gms.google-services`, el BOM de Firebase, `firebase-firestore`, `firebase-auth` y `kotlinx-coroutines-play-services`. Usar las versiones vigentes y verificar que compilen con la versión de AGP del proyecto.
2. `miUid()`: si no hay usuario, hace `signInAnonymously()` y devuelve el uid.
3. `enviar(nombre, puntaje, comboMax)`: escribe en `ranking/{uid}` los campos `nombre`, `puntaje`, `comboMax` y `actualizado = FieldValue.serverTimestamp()`. Si el puntaje es menor al guardado, **no** es un error: devuelve `Result.success` sin escribir (leer antes, o capturar el rechazo de las reglas).
4. `top(limite)`: `orderBy("puntaje", DESCENDING).limit(limite)`, mapeado a `EntradaRanking`.
5. Todo error de red devuelve `Result.failure` (no lanza excepciones).

**Criterios de aceptación**
- Dado un jugador que envía 500 y luego 300, el ranking conserva 500.
- Dado un jugador que cambia su nombre y envía un puntaje igual o mayor, el ranking muestra el nombre nuevo.
- Dado dos dispositivos con jugadores distintos, ambos aparecen en el ranking.
- Dado un nombre de 1 o de 13 caracteres, las reglas rechazan la escritura.
- Dado sin internet, `top()` y `enviar()` devuelven `Result.failure`.

**Verificación:** `./gradlew assembleDebug` y prueba con 2 dispositivos o emuladores.

---

### SPEC-U5: Revisión y cierre

- Revisar los PR de Lucas y Matias contra su spec (criterios de aceptación y archivos permitidos).
- Probar el ciclo completo: Aviso → Nombre (primera vez) → Menú → Iniciar Partida (perder, segunda vida) → Endless (segunda vida y recarga) → Modo Corazón → Ranking.
- Probar los dos modos de control en un dispositivo real.
- Actualizar `README.md` con los modos nuevos.

---

## 6. Specs de Lucas

### SPEC-L1: Nombre de jugador (SQLite) y `MenuScreen`

**Objetivo:** pedir el nombre la primera vez, guardarlo en SQLite y permitir cambiarlo; dejar el menú final.

**Archivos:** crear `data/JugadorDbHelper.kt`, `data/JugadorSqliteRepository.kt`, `util/FiltroNombre.kt`, `FiltroNombreTest.kt`; reemplazar el stub `ui/nombre/NombreScreen.kt`; modificar `MenuScreen.kt`; modificar **solo** los bloques `AVISO`, `MENU` y `NOMBRE` de `AppNavigation.kt`; cambiar la línea de `jugador` en `Repos.kt`. **Depende de:** U0.

**Requisitos**
1. SQLite con `SQLiteOpenHelper` (sin Room ni dependencias nuevas). Base `todobien.db`, tabla `jugador(id INTEGER PRIMARY KEY CHECK (id = 1), nombre TEXT NOT NULL)`. Una sola fila. Accesos en `Dispatchers.IO`.
2. `FiltroNombre.esValido(nombre)`: tras `trim()`, entre 2 y 12 caracteres, solo letras (con acentos y ñ), números y espacios, y que no contenga ninguna palabra de una lista bloqueada que el equipo completa en un array al principio del archivo.
3. `NombreScreen(editar: Boolean)`: título «¿Cómo te llamamos?», texto de ayuda «Usá un apodo, no tu nombre completo», campo de texto, mensaje de error visible si no es válido, botón deshabilitado hasta que sea válido. Si `editar` es `true`, el campo viene precargado y el botón dice «Guardar».
4. Flujo: `AVISO → (si no hay nombre) NOMBRE → MENU`. Si ya hay nombre, `AVISO → MENU`. En modo primera vez, al confirmar se navega a `MENU` con `popUpTo(NOMBRE) { inclusive = true }`. En modo editar, al guardar se hace `popBackStack()`.
5. `MenuScreen` final, con estos botones y en este orden: **Iniciar Partida**, **Endless**, **Modo Corazón** (usa `onArcade`), **Opciones**. Arriba del título o debajo del subtítulo, un texto «Hola, {nombre}» con un ícono de lápiz que llama a `onEditarNombre`. El nombre se lee del repositorio en el bloque `MENU` y se pasa por parámetro.
6. El nombre se guarda tal cual lo escribió el jugador, sin el espacio inicial y final.

**Criterios de aceptación**
- Dado un primer inicio, se muestra la pantalla de nombre después del aviso y no se puede continuar con un nombre inválido.
- Dado el nombre guardado, al cerrar y abrir la app se va directo al menú y se ve el nombre.
- Dado el menú, tocar el lápiz abre la edición con el nombre actual; al guardar, el menú muestra el nombre nuevo.
- Dado un nombre de 1 carácter, de 13 caracteres, con símbolos, o con una palabra bloqueada, entonces es inválido.
- Los cuatro botones del menú navegan a su destino.

**Verificación:** `./gradlew testDebugUnitTest --tests "*FiltroNombreTest"` y `./gradlew assembleDebug`.

---

### SPEC-L2: Preferencias (DataStore) y pantalla de Opciones

**Objetivo:** persistir color, modo de control y récords; elegir el modo de control en Opciones.

**Archivos:** crear `data/PreferenciasDataStore.kt`; modificar `OpcionesScreen.kt` y el bloque `OPCIONES` de `AppNavigation.kt`; cambiar la línea de `prefs` en `Repos.kt`. **Depende de:** U0.

**Requisitos**
1. Implementar `PreferenciasRepo` con `datastore-preferences` (ya está en el proyecto). Claves y valores por defecto: color `0xFFFF4D79`, modo de control `TACTIL`, mejor puntaje 0, mejor combo 0, récord Endless 0.
2. `registrarArcade(puntaje, combo)` guarda cada valor solo si supera al anterior. `registrarEndless(n)` guarda y devuelve `true` solo si `n` supera el récord.
3. `OpcionesScreen`: mantener los switches de música y vibración y agregar una sección **«Control del corazón»** con dos opciones excluyentes: «Tocar la pantalla» e «Inclinar el celular». Se lee y se escribe con `Repos.prefs`.
4. Si el dispositivo no tiene acelerómetro (`SensorManager.getDefaultSensor(TYPE_ACCELEROMETER) == null`), la opción «Inclinar» aparece deshabilitada con una nota, y el modo queda en `TACTIL`.
5. Los switches de música y vibración también deben persistir.

**Criterios de aceptación**
- Dado un cambio de modo de control, al cerrar y abrir la app se mantiene.
- Dado `registrarEndless(5)` y luego `registrarEndless(3)`, el récord es 5 y la segunda llamada devuelve `false`.
- Dado un dispositivo sin acelerómetro, la opción de inclinar no se puede elegir.

**Verificación:** `./gradlew assembleDebug` y prueba manual.

---

### SPEC-L3: Control por inclinación

**Objetivo:** mover el corazón inclinando el teléfono.

**Archivos:** crear `ui/corazon/EfectoInclinacion.kt`. **Depende de:** U0.

**Interfaz**
```kotlin
@Composable
fun EfectoInclinacion(activo: Boolean, onCarril: (Int) -> Unit)
```

**Requisitos**
1. Cuando `activo` es `true`, registrar el `SensorManager` con `TYPE_ACCELEROMETER` y `SENSOR_DELAY_GAME`. Desregistrar en `onDispose`, cuando `activo` pasa a `false` y cuando la app pasa a segundo plano.
2. Con la app en vertical, usar `values[0]` (eje X). Inclinar hacia la **izquierda** da un valor **positivo**; hacia la **derecha**, **negativo** (verificar en un dispositivo real).
3. Filtro pasa-bajos: `x = 0,8 × x + 0,2 × nuevo`.
4. Mapeo a carril: `x > 2,5` → carril 0 (izquierda); `x < −2,5` → carril 2 (derecha); en el medio → carril 1. Histéresis de 0,5 para evitar que salte entre carriles.
5. Llamar a `onCarril` **solo cuando el carril cambia**.
6. Si no hay acelerómetro, no hace nada y no crashea.

**Criterios de aceptación**
- Dado un dispositivo real, inclinar a la izquierda lleva al carril 0, plano al carril 1, a la derecha al carril 2.
- Dado un temblor leve de la mano, el carril no cambia de forma errática.
- Dado `activo = false`, no hay listener registrado (comprobar que el consumo de batería no sube).

**Verificación:** `./gradlew assembleDebug` y prueba en teléfono real (el emulador permite simular con «Virtual sensors»).

---

### SPEC-L4: Pantalla de preparación y pantalla del modo arcade

**Objetivo:** elegir el color del corazón y jugar el modo completo.

**Archivos:** reemplazar los stubs `ArcadeSetupScreen.kt` y `ArcadeScreen.kt`; modificar solo los bloques `ARCADE_SETUP` y `ARCADE` de `AppNavigation.kt`. **Depende de:** U0, y de U2 + M2 para la versión final.

**Requisitos**
1. `ArcadeSetupScreen`: selector de color con una **paleta de 8 colores** y un **control de matiz** (`Slider` de 0 a 360 con `Color.hsv(h, 0.8f, 1f)`). Vista previa del corazón en el color elegido. Botones **Jugar** (guarda el color con `Repos.prefs.setColorCorazon` y navega a `ARCADE`) y **Ver ranking**.
2. El selector arranca con el color guardado.
3. `ArcadeScreen`: crea `MotorCorazon(ConfigCorazon.ARCADE)` con `remember`, muestra `CorazonJuego` a pantalla completa con `HudArcade` (de M2) encima, y lee color y modo de control de `Repos.prefs`.
4. Al terminar (`onTermino`): navega a `ARCADE_GAME_OVER` con `puntaje`, `comboMax` y `segundos`, con `popUpTo(ARCADE) { inclusive = true }`.
5. `BackHandler`: Atrás vuelve al menú sin guardar nada.

**Criterios de aceptación**
- Dado un color elegido, el corazón del juego usa ese color y se mantiene al reabrir la app.
- Dado el juego terminado, se llega a la pantalla final con los datos correctos.
- Dado el modo de control en Opciones, el modo arcade lo respeta.

**Verificación:** `./gradlew assembleDebug` y prueba manual.

---

## 7. Specs de Matias

### SPEC-M1: Diseño y assets

**Entregables**
- Figma con: HUD del modo arcade, HUD de la recuperación, pantalla `RecuperacionIntro`, Game Over del arcade, ranking, `NombreScreen`, botón del menú de «Modo Corazón» (nombre a definir) e **indicador de segunda vida disponible**.
- Íconos vectoriales en `res/drawable`: `corazon_malo_1..4.xml` y `corazon_bueno_1..3.xml` (**formas distintas entre sí, no solo color**), `corazon_escudo.xml`, `corazon_segunda_vida.xml`. El corazón del jugador **no** va como asset: se dibuja en código para poder teñirlo.
- Textos en `strings.xml` con prefijo `corazon_`: «¡Escudo emocional!», «Recuperá tus buenas vibras», «¡Segunda vida recargada!», etc.
- Colores nuevos en `ui/theme/ColorCorazon.kt`.

**Criterios de aceptación:** los rojos y verdes se distinguen entre sí en escala de grises. Todos los íconos se ven bien a 48 dp.

---

### SPEC-M2: HUD del minijuego en Compose

**Archivos:** crear `ui/corazon/HudCorazon.kt`. **Depende de:** U0 (contratos).

**Interfaz**
```kotlin
@Composable fun HudArcade(hud: EstadoHud, modifier: Modifier = Modifier)
@Composable fun HudRecuperacion(estabilidad: Int, modifier: Modifier = Modifier)
```

**Requisitos**
1. `HudArcade`: barra de Estabilidad (0 a 100, animada con `animateFloatAsState`, pasos de 25 marcados), 5 casillas del escudo (se llenan con `progresoEscudo` y se iluminan todas cuando `escudo` es `true`), puntaje, combo y multiplicador (`×2`, `×3`…).
2. `HudRecuperacion`: barra «Buenas vibras» de 0 a 100 con marcas cada 25.
3. Usar la paleta del proyecto. Incluir `@Preview` con datos de ejemplo para cada estado.

**Criterios de aceptación:** con `@Preview`, se ven correctos el escudo apagado, a medio llenar y activo, y una estabilidad de 0, 50 y 100.

---

### SPEC-M3: Game Over del arcade y Ranking

**Archivos:** reemplazar los stubs `ArcadeGameOverScreen.kt` y `RankingScreen.kt`; modificar solo los bloques `ARCADE_GAME_OVER` y `RANKING` de `AppNavigation.kt`. **Depende de:** U0.

**Requisitos**
1. `ArcadeGameOverScreen(puntaje, comboMax, segundos)`: muestra los tres datos, el mejor puntaje y el mejor combo guardados, y una marca «¡Nuevo récord!» si corresponde. Llama a `Repos.prefs.registrarArcade` al entrar.
2. Al entrar también envía el puntaje: `Repos.ranking.enviar(nombreGuardado, puntaje, comboMax)`, con el nombre de `Repos.jugador.obtenerNombre()`. Muestra el estado: *Enviando…*, *Enviado*, o *No se pudo enviar* con botón **Reintentar**. El juego no se bloquea si falla.
3. Botones: **Jugar de nuevo** (a `ARCADE`), **Ver ranking**, **Menú**.
4. `RankingScreen`: `LazyColumn` con el top 20 (posición, nombre, puntaje). Resalta la fila cuyo `uid` coincide con `Repos.ranking.miUid()`. Estados de cargando, vacío y error con botón **Reintentar**.
5. Las dos pantallas se prueban con `FakeRankingRepository`; cuando Uriel termine U4 cambia solo la línea de `Repos`.

**Criterios de aceptación**
- Dado un fallo de red simulado, se ve el error y el reintento, y la app no crashea.
- Dado el ranking con 25 entradas, solo se ven 20.
- Dado el jugador dentro del top, su fila está resaltada.

---

### SPEC-M4: `RecuperacionIntro`

**Archivos:** crear `ui/corazon/RecuperacionIntro.kt`.

```kotlin
@Composable fun RecuperacionIntro(onListo: () -> Unit, modifier: Modifier = Modifier)
```

**Requisitos:** pantalla breve, con el mensaje «Todavía no terminó… recuperá tus buenas vibras», una explicación de una línea de cómo se juega (verdes suben, rojos bajan, llegá al 100%) y un botón **Empezar**. No se cierra sola: el jugador toca el botón. Incluir `@Preview`.

---

### SPEC-M5: Endless base

**Objetivo:** que Endless exista y funcione. **Merge antes que U3.** **Depende de:** U0, L2 (para el récord; mientras tanto usar `FakePreferenciasRepo`), L1 (botón en el menú).

**Archivos:** modificar `JuegoScreenViewModel.kt`, `JuegoScreen.kt` (solo lo necesario para mostrar rondas sin tope) y los bloques `JUEGO` y `GAME_OVER` de `AppNavigation.kt`, y `GameOverScreen.kt`. **No tocar** la lógica de segunda vida (la agrega U3).

**Requisitos**
1. Con `modo == ENDLESS`: no hay límite de rondas ni pantalla de victoria. El HUD muestra «Ronda N» (sin «/ 10»).
2. Las situaciones salen de una cola barajada de todas las disponibles. Al agotarse se vuelve a barajar, **sin repetir la última situación** al empezar la nueva cola.
3. Dificultad creciente: el tiempo por ronda arranca en 25 s y baja **1 s cada 5 rondas superadas**, con un mínimo de 10 s.
4. Al perder: `rondasSuperadas = rondaActual − 1`. Llamar a `Repos.prefs.registrarEndless(rondasSuperadas)` y navegar a `Rutas.gameOver(modo, rondasSuperadas, nuevoRecord)`.
5. `GameOverScreen`: en ENDLESS muestra «Rondas superadas: N» y «¡Nuevo récord!» si `nuevoRecord`. «Reintentar» vuelve al mismo modo.
6. El modo OLEADAS sigue idéntico al actual.

**Criterios de aceptación**
- Dado Endless, se puede superar la ronda 10 sin ver la pantalla de victoria.
- Dado Endless en la ronda 25, el tiempo por ronda es de 20 s.
- Dado Endless, no se repiten situaciones hasta agotar toda la lista.
- Dado un Game Over en Endless, «Reintentar» inicia Endless, no OLEADAS.
- Dado OLEADAS, nada cambia.

**Verificación:** `./gradlew assembleDebug` y `./gradlew testDebugUnitTest`.

---

### SPEC-M6: Indicador visual de segunda vida

**Objetivo:** que el jugador vea si la segunda vida está disponible. **Depende de:** U0 (parámetro y campo ya creados), M1 (ícono).

**Archivos:** modificar `JuegoScreen.kt` (solo `BarraHud` o el encabezado).

**Requisitos**
1. Mostrar el ícono `corazon_segunda_vida` en el HUD del juego principal, en OLEADAS **y** ENDLESS, según el parámetro `segundaVidaDisponible`.
2. Disponible: ícono a color con un brillo suave. No disponible: ícono apagado (gris, sin brillo).
3. Al pasar de no disponible a disponible, una animación breve y el texto «¡Segunda vida recargada!». No debe animarse en la primera composición.
4. Debe tener `contentDescription` para accesibilidad («Segunda vida disponible» / «Segunda vida usada»).

**Criterios de aceptación**
- Dado el inicio de una partida, el ícono se ve activo.
- Dado el uso de la segunda vida, el ícono pasa a apagado.
- Dado Endless, al completar la ronda 10 con la segunda vida usada, el ícono se reactiva con el aviso.

---

## 8. Orden de trabajo y dependencias

```
U0 ─┬─> U1 ──> U2 ──┬─> L4 (versión final)
    │               └─> U3 ──> M6
    ├─> L1, L2, L3  (en paralelo)
    ├─> M1, M2, M3, M4  (en paralelo)
    ├─> M5 ──────────────> U3   (M5 se mergea ANTES que U3)
    └─> U4 ──> M3 (cambia a ranking real)
```

| Fase | Quién | Specs |
|---|---|---|
| 0 | Uriel | U0 |
| 1 (en paralelo) | Uriel / Lucas / Matias | U1 · L1, L2, L3 · M1, M2, M4, M5 |
| 2 | Uriel / Lucas / Matias | U2, U4 (parte A manual) · L4 · M3 |
| 3 | Uriel / Matias | U3 · M6 |
| 4 | Todos | Calibración, pruebas en dispositivos reales, U5 |

**Calibración (fase 4):** los valores de la sección 4 son un punto de partida. Volver con Estabilidad al 100% es intencional (el objetivo es didáctico y que sea difícil perder), pero la recuperación en sí tiene que seguir siendo ganable con esfuerzo: probar con distintos `probVerde` hasta que llegar al 100% no sea trivial ni frustrante.

---

## 9. Reglas de Git

1. `main` es lo entregable; `develop` es la integración. Nadie sube directo a ninguna de las dos.
2. Una rama por spec: `feature/spec-u1`, `feature/spec-l2`, etc., desde `develop`.
3. Commits chicos con mensaje claro, por ejemplo `feat(SPEC-L1): agrega pantalla de nombre`.
4. Antes de abrir un PR: traer lo último de `develop`, compilar y correr los tests.
5. Todo PR lo revisa Uriel contra la spec: archivos permitidos, criterios de aceptación y verificación.
6. **Archivos con dueño único** (si otro necesita un cambio, lo pide): `build.gradle.kts` y `libs.versions.toml` (Uriel), `Rutas` y `Repos.kt` (Uriel; los demás solo cambian **su** línea de `Repos`), `MenuScreen.kt` (Lucas), `JuegoScreenViewModel.kt` (primero Matias con M5, después Uriel con U3).
7. Nunca `git push --force` en `develop` ni en `main`. No subir `local.properties`.
