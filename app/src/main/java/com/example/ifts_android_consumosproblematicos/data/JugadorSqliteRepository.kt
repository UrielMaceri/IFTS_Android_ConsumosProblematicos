package com.example.ifts_android_consumosproblematicos.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JugadorSqliteRepository(context: Context) : JugadorRepository {
    private val dbHelper = JugadorDbHelper(context.applicationContext)

    override suspend fun obtenerNombre(): String? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            JugadorDbHelper.TABLA_JUGADOR,
            arrayOf(JugadorDbHelper.COLUMNA_NOMBRE),
            "${JugadorDbHelper.COLUMNA_ID} = ?",
            arrayOf("1"),
            null,
            null,
            null
        )
        cursor.use { c ->
            if (c.moveToFirst()) {
                c.getString(c.getColumnIndexOrThrow(JugadorDbHelper.COLUMNA_NOMBRE))
            } else {
                null
            }
        }
    }

    override suspend fun guardarNombre(nombre: String): Unit = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(JugadorDbHelper.COLUMNA_ID, 1)
            put(JugadorDbHelper.COLUMNA_NOMBRE, nombre)
        }
        db.insertWithOnConflict(
            JugadorDbHelper.TABLA_JUGADOR,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }
}
