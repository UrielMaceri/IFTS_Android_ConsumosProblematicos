# Relato de Partida — ¿Todo Bien?

> **Nota:** los eventos concretos (situaciones, textos, imágenes) todavía no están definidos. Se van a guardar en una base de datos de eventos (formato a definir, posiblemente JSON), y el juego elige uno al azar en cada turno. Este relato describe el **flujo completo** de una partida, usando un evento de ejemplo genérico para ilustrar el formato — no una secuencia fija de historias.

## Menú principal

El jugador abre la app y llega a un menú con dos opciones:

- **Iniciar Partida**
- **Endless**
- **Opciones** _(opciones de sonido)_

## Arranca la partida

La pantalla muestra dos barras:

- 🟢 **Estabilidad emocional: 100%**
- 🟣 **Presión social: 50%** 

Empieza el bucle de eventos.

## Bucle de eventos
Si se elije:
-  **Iniciar Partida**: se repite hasta completar 10, o hasta Game Over
-  **Endless**: se repite si o si hasta Game Over. Tiene un contador de rondas exclusivo de esta opción.

En cada turno, el juego selecciona **al azar** un evento de la base de datos de eventos. Cada evento tiene:

- Un **texto** que describe la situación
- Una **imagen** asociada a esa situación
- **Tres opciones** de respuesta: negativa, neutral y positiva, cada una con su propio efecto sobre Estabilidad y Presión
- Un **contador de tiempo** para elegir

### Ejemplo de evento (formato genérico)

**Situación:** *[texto de la situación — se completa desde la base de eventos]*
**Imagen:** *[imagen asociada al evento]*

Aclaración: Estabilidad ↓ (malo) | ↑ (bueno) // Presión ↓ (bueno) | ↑ (malo)

- 🔴 Opción negativa → Estabilidad ↓ | Presión ↓
- 🟡 Opción neutral → Estabilidad y/o Presión suben o bajan según el evento (no siempre es la mejor opción)
- 🟢 Opción positiva → Estabilidad ↑ (o se mantiene) | Presión ↓

El jugador elige una opción antes de que el contador llegue a 0.

- Si el tiempo se agota sin elegir → la Estabilidad no cambia, pero la Presión sube.
- Si la Presión llega al 100% → deja de subir y en su lugar empieza a bajar la Estabilidad, a un ritmo mucho mayor que el que tenía la Presión.

Resuelto el evento, el juego vuelve a elegir otro al azar de la base y el ciclo se repite.

## Fin de la partida

- **Game Over:** si en cualquier momento la Estabilidad emocional llega a 0%, se muestra una pantalla de Game Over y hay que reiniciar la partida.
- **Victoria:** si el jugador completa 10 eventos seguidos sin que la Estabilidad llegue a 0%, se muestra una pantalla de cierre positivo — el personaje aprendió a manejar mejor sus emociones y puede seguir con su vida.
