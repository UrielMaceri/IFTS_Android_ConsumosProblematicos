# Plan de Desarrollo: "¿Todo Bien?"

---

## FASE 1 — Pantallas y Flujo Visual (UI y Navegación)

### 1. Pantalla del Juego (JuegoScreen.kt)
- **Barras de Estado**:
  - Indicador de Estabilidad Emocional (barra de 0 a 100).
  - Indicador de Presión Social (barra de 0 a 100).
  - Contador de ronda actual (ej. Ronda 3 / 10).
- **Contenedor Visual**: Espacio asignado para la imagen/ilustración de la situación.
- **Relato de la Situación**: Tarjeta con el texto de la situación actual.
- **3 Botones de Opciones**: Botones interactivos para tomar decisiones (Opción A, Opción B, Opción C).
- **Header / Botón de Pausa**: Opción para pausar o volver al menú principal.

### 2. Pantalla de Game Over (GameOverScreen.kt)
- Título/Aviso de fin de juego (cuando la estabilidad llega a 0 o la presión a 100).
- Mensaje o texto reflexivo sobre el motivo de la derrota.
- Botón **Reintentar** (reinicia la partida desde la ronda 1).
- Botón **Volver al Menú**.

### 3. Pantalla de Éxito / Victoria (SuccessScreen.kt)
- Mensaje de felicitaciones al completar las 10 rondas.
- Resumen con las estadísticas finales alcanzadas en Estabilidad y Presión.
- Botón **Volver a Jugar**.
- Botón **Volver al Menú**.

### 4. Actualización de Navegación (AppNavigation.kt)
- Registro de las nuevas rutas: `Rutas.GAME_OVER` y `Rutas.SUCCESS`.
- Manejo de callbacks entre el juego, el fin de partida y el retorno al menú.

---

## FASE 2 — Carga de Datos (JSON)

### 1. Archivo de Situaciones (situaciones.json)
- Creación de la carpeta `app/src/main/assets/` y ubicación del JSON con el listado de situaciones, opciones y los efectos de cada opción (+/- Estabilidad, +/- Presión).

### 2. Repositorio / Lector JSON (SituacionesRepository.kt)
- Lógica de lectura y deserialización del JSON.
- Algoritmo de selección aleatoria (shuffle) de situaciones para asegurar que no se repitan dentro de las 10 rondas.

---

## FASE 3 — Lógica de Negocio y Estado (MVVM)

### 1. JuegoViewModel.kt
- Manejo del estado del juego (`StateFlow<JuegoUiState>`), incluyendo:
  - Situación actual cargada.
  - Estado del jugador (Estabilidad y Presión).
  - Número de ronda actual.
- Lógica para procesar la opción elegida:
  - Sumar/restar puntos de estabilidad y presión.
  - Verificar condición de Game Over (`Estabilidad <= 0` o `Presión >= 100`).
  - Verificar condición de Victoria (completar la ronda 10).
  - Transición a la siguiente situación aleatoria.

---

## FASE 4 — Estilos, Animaciones y Pulido Final

### 1. Sistema de Diseño y Tema (ui/theme/)
- Ajuste fino de colores (tonos de alerta para presión alta o estabilidad baja).
- Tipografía y formas de tarjetas (Cards y Buttons).

### 2. Persistencia y Configuración (OpcionesScreen.kt)
- Persistencia del estado de los switches de Música y Vibración usando DataStore / SharedPreferences.

### 3. Animaciones y Efectos
- Animación suave en el llenado/vaciado de las barras de estado (`animateFloatAsState`).
- Transición entre situaciones (animación de entrada/salida de tarjetas).
- Feedback visual (cambio de color momentáneo o vibración ligera) al elegir una opción según su impacto.

### 4. Pruebas y QA
- Verificación del ciclo completo (Aviso Inicial -> Menú -> Juego -> Game Over / Éxito -> Reintentar).
- Verificación de retención de estado ante cambios de orientación de pantalla.

---

## FASE 5 — Modo Endless (Modo Infinito)

### 1. Habilitación en la Interfaz de Usuario
- Reincorporar el botón **Endless** en `MenuScreen.kt`.
- Pasar el parámetro `ModoJuego.ENDLESS` a través de la navegación hacia `JuegoScreen`.

### 2. Lógica de Juego Infinito
- Eliminar el límite de 10 rondas en `JuegoScreenViewModel.kt` para el modo `ENDLESS`.
- Generar un bucle infinito de situaciones aleatorias repitiendo/rebarajando la lista de situaciones al agotarse.
- Aumento progresivo de la dificultad (ej. reducción paulatina del tiempo por ronda o mayor incremento de la presión social conforme aumentan las rondas).

### 3. Pantalla de Fin de Juego Endless
- Al llegar la estabilidad a 0 o la presión a 100, mostrar el número máximo de rondas/eventos sobrevivientes alcanzados.
- Registro/guardado del récord personal de rondas alcanzadas en el modo Endless.
