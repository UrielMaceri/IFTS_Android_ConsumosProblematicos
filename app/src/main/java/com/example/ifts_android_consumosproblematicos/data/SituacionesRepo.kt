package com.example.ifts_android_consumosproblematicos.data

import android.content.Context
import com.example.ifts_android_consumosproblematicos.model.Efecto
import com.example.ifts_android_consumosproblematicos.model.Opcion
import com.example.ifts_android_consumosproblematicos.model.Situacion
import com.example.ifts_android_consumosproblematicos.model.TipoOpcion
import org.json.JSONArray

class SituacionesRepository(private val context: Context) {

    private fun cargarJsonDesdeRecurso(): String {
        return try {
            context.assets.open("situaciones.json").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            try {
                javaClass.classLoader?.getResourceAsStream(
                    "com/example/ifts_android_consumosproblematicos/data/situaciones.json"
                )?.bufferedReader()?.use { it.readText() } ?: "[]"
            } catch (_: Exception) {
                "[]"
            }
        }
    }

    fun obtenerTodas(): List<Situacion> {
        val jsonString = cargarJsonDesdeRecurso()
        val lista = mutableListOf<Situacion>()

        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val texto = item.optString("texto", "")
                val imagen = item.optString("imagen", "")
                val opcionBuenaText = item.optString("opcionBuena", "")
                val opcionNeutralText = item.optString("opcionNeutral", "")
                val opcionMalaText = item.optString("opcionMala", "")

                val opciones = listOf(
                    Opcion(
                        texto = opcionBuenaText,
                        tipo = TipoOpcion.Positiva,
                        efecto = Efecto(estabilidad = 10, presion = -20)
                    ),
                    Opcion(
                        texto = opcionNeutralText,
                        tipo = TipoOpcion.Neutral,
                        efecto = Efecto(estabilidad = 0, presion = -10)
                    ),
                    Opcion(
                        texto = opcionMalaText,
                        tipo = TipoOpcion.Negativa,
                        efecto = Efecto(estabilidad = -15, presion = 10)
                    )
                ).shuffled()

                lista.add(
                    Situacion(
                        id = i + 1,
                        texto = texto,
                        imagen = imagen,
                        opciones = opciones
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return lista
    }

    fun obtenerSituacionesAleatorias(cantidad: Int = 10): List<Situacion> {
        val todas = obtenerTodas()
        return todas.shuffled().take(cantidad)
    }
}
