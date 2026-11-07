package com.example.ifts_android_consumosproblematicos.util

object FiltroNombre {
    val PALABRAS_BLOQUEADAS = arrayOf(
        "admin", "administrator", "root", "null", "undefined",
        "puto", "puta", "mierda", "carajo", "boludo", "boluda",
        "pelotudo", "pelotuda", "concha", "pija", "orto", "hijo de puta"
    )

    fun esValido(nombre: String): Boolean {
        val recortado = nombre.trim()
        if (recortado.length !in 2..12) return false

        val regexPermitido = Regex("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑüÜ ]+$")
        if (!regexPermitido.matches(recortado)) return false

        val contieneBloqueada = PALABRAS_BLOQUEADAS.any { palabra ->
            recortado.contains(palabra, ignoreCase = true)
        }
        if (contieneBloqueada) return false

        return true
    }
}
