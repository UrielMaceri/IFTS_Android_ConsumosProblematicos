package com.example.ifts_android_consumosproblematicos.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class JugadorDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    NOMBRE_BASE_DATOS,
    null,
    VERSION_BASE_DATOS
) {
    companion object {
        private const val NOMBRE_BASE_DATOS = "todobien.db"
        private const val VERSION_BASE_DATOS = 1

        const val TABLA_JUGADOR = "jugador"
        const val COLUMNA_ID = "id"
        const val COLUMNA_NOMBRE = "nombre"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val crearTabla = """
            CREATE TABLE $TABLA_JUGADOR (
                $COLUMNA_ID INTEGER PRIMARY KEY CHECK ($COLUMNA_ID = 1),
                $COLUMNA_NOMBRE TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(crearTabla)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_JUGADOR")
        onCreate(db)
    }
}
