# Ajustes y mejoras (Revisión y Cierre)

Documento de seguimiento interno de tareas y ajustes pendientes para ejecutar durante la fase final (**SPEC-U5**).

# Bugs
---

### Conexión de Audio y Sonido (SoundManager)
- [x] **Vincular estado de música:** Conectado `Repos.prefs.musicaActivada` con `SoundManager.musicaHabilitada` en `MainActivity`. Al pasar a `false`, se ejecuta automáticamente `detenerMusica()`.
- [x] **Vincular estado de efectos/vibración:** Conectado `Repos.prefs.vibracionActivada` con `SoundManager.efectosHabilitados` en `MainActivity`.

### Limpieza de Repositorios Fake
- [x] **Mover a `src/test`:**
  - `data/FakeJugadorRepository.kt` (Movido a `app/src/test/java/...`)
  - `data/ranking/FakeRankingRepository.kt` (Movido a `app/src/test/java/...`)
  - `data/FakePreferenciasRepo.kt` (Movido a `app/src/test/java/...`)

### Musica del minijuego
- [x] La musica del minijuego en el modo segunda vida no se estaba reproduciento correctamente.
---
# Mejoras
---

### Mejoras Visuales y Textos Flotantes
- [x] **Textos flotantes al impactar obstáculos:**
  - Al recolectar **verdes**: frases inspiracionales flotantes (*"¡Buena vibra!"*, *"¡Empatía!"*, *"¡Respirá!"*, *"¡Fuerza!"*, *"¡Vos podés!"*) en color verde.
  - Al chocar **rojos**: advertencias flotantes (*"¡Cuidado!"*, *"¡Presión!"*, *"¡Estrés!"*, *"¡Tensión!"*) en color rojo.
  - Al activarse el **escudo**: texto flotante *"¡ESCUDO ACTIVADO!"* en color cian/azul escudo (`#00E5FF`).
  - Al chocar contra un **rojo con escudo activo**: texto flotante *"¡No me afecta!"* en color cian/azul escudo (`#00E5FF`).


### Indicadores de Control e Instrucciones Iniciales
- [x] **Overlay de ayuda/controles (3 segundos al iniciar):**
  - **Modo TÁCTIL:** Muestra la imagen semitransparente `touch.png` a ambos lados (izq/der) con el texto *"Toca aquí"* durante 3 segundos al iniciar.
  - **Modo INCLINACIÓN (Acelerómetro):** Muestra la imagen semitransparente `rotate.png` a ambos lados (izq/der) con el texto *"Mueve"* durante 3 segundos al iniciar.

### Audio y Música Temática
- [x] **Música dedicada para el minijuego del corazón:** Integrado `backgroundminigame.ogg` (`R.raw.backgroundminigame`) en el minijuego del corazón (`ArcadeScreen` y `RecuperacionScreen`), con manejo de ciclo de vida (pausa/reanudación y detención al salir).

### Opción de sonidos
- [x] **Opción dedicada** a mutear sonidos de botones y efectos (`efectosActivados`), separada de Música y Vibración en `OpcionesScreen` y `PreferenciasDataStore`.

### Desbloqueables
- [x] **Progreso de desbloqueo:** Los modos "Endless" y "Minijuego corazón" inician bloqueados por primera vez y se desbloquean al ganar/completar el modo Oleadas (`oleadasCompletado` persistido en DataStore).
  - Al seleccionar un modo bloqueado, la tarjeta muestra borde rojo (`#E74C3C`) e ícono de candado, manteniendo el color de fondo.
  - La descripción del modo bloqueado muestra *"Juega al modo oleadas para desbloquear esta función."*.
  - El botón principal cambia a *"BLOQUEADO"* e inhabilita el acceso hasta desbloquearlo.

---