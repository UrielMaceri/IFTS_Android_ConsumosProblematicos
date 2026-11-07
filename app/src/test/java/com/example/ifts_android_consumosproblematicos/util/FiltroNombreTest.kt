package com.example.ifts_android_consumosproblematicos.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FiltroNombreTest {

    @Test
    fun nombreValido_retornaTrue() {
        assertTrue(FiltroNombre.esValido("Lucas"))
        assertTrue(FiltroNombre.esValido("  María 12  "))
        assertTrue(FiltroNombre.esValido("Ñandú"))
        assertTrue(FiltroNombre.esValido("  ok  "))
    }

    @Test
    fun nombreDeUnCaracter_retornaFalse() {
        assertFalse(FiltroNombre.esValido("A"))
        assertFalse(FiltroNombre.esValido("  x  "))
    }

    @Test
    fun nombreDeTreceCaracteres_retornaFalse() {
        assertFalse(FiltroNombre.esValido("1234567890123"))
        assertFalse(FiltroNombre.esValido("NombreLargoDoce+1"))
    }

    @Test
    fun nombreConSimbolos_retornaFalse() {
        assertFalse(FiltroNombre.esValido("Lucas!"))
        assertFalse(FiltroNombre.esValido("User_1"))
        assertFalse(FiltroNombre.esValido("Hola@Mundo"))
    }

    @Test
    fun nombreConPalabraBloqueada_retornaFalse() {
        assertFalse(FiltroNombre.esValido("admin"))
        assertFalse(FiltroNombre.esValido("  ADMIN  "))
        assertFalse(FiltroNombre.esValido("SoyPuto"))
        assertFalse(FiltroNombre.esValido("root123"))
    }
}
