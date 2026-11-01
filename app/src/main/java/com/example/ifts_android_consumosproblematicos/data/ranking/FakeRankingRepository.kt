package com.example.ifts_android_consumosproblematicos.data.ranking

class FakeRankingRepository : RankingRepository {
    private val listaRanking = mutableListOf<EntradaRanking>()

    override suspend fun miUid(): String = "fake_user_uid"

    override suspend fun enviar(nombre: String, puntaje: Int, comboMax: Int): Result<Unit> {
        val uid = miUid()
        val index = listaRanking.indexOfFirst { it.uid == uid }
        if (index != -1) {
            if (puntaje > listaRanking[index].puntaje) {
                listaRanking[index] = EntradaRanking(uid, nombre, puntaje, comboMax)
            }
        } else {
            listaRanking.add(EntradaRanking(uid, nombre, puntaje, comboMax))
        }
        return Result.success(Unit)
    }

    override suspend fun top(limite: Int): Result<List<EntradaRanking>> {
        val ordenado = listaRanking.sortedByDescending { it.puntaje }.take(limite)
        return Result.success(ordenado)
    }
}
