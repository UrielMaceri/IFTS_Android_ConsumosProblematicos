package com.example.ifts_android_consumosproblematicos.data

import android.content.Context
import com.example.ifts_android_consumosproblematicos.data.ranking.FakeRankingRepository
import com.example.ifts_android_consumosproblematicos.data.ranking.RankingRepository

object Repos {
    lateinit var jugador: JugadorRepository
    lateinit var prefs: PreferenciasRepo
    lateinit var ranking: RankingRepository

    fun init(context: Context) {
        jugador = FakeJugadorRepository()
        prefs = FakePreferenciasRepo()
        ranking = FakeRankingRepository()
    }
}
