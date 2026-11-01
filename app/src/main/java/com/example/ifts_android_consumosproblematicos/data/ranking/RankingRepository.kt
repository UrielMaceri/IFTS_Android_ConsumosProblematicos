package com.example.ifts_android_consumosproblematicos.data.ranking

data class EntradaRanking(
    val uid: String,
    val nombre: String,
    val puntaje: Int,
    val comboMax: Int
)

interface RankingRepository {
    suspend fun miUid(): String?
    suspend fun enviar(nombre: String, puntaje: Int, comboMax: Int): Result<Unit>
    suspend fun top(limite: Int = 20): Result<List<EntradaRanking>>
}
