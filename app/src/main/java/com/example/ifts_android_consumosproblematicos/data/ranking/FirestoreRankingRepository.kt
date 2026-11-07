package com.example.ifts_android_consumosproblematicos.data.ranking

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestoreRankingRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : RankingRepository {

    override suspend fun miUid(): String? {
        return try {
            val usuarioActual = auth.currentUser
            if (usuarioActual != null) {
                usuarioActual.uid
            } else {
                val resultadoAuth = auth.signInAnonymously().await()
                resultadoAuth.user?.uid
            }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun enviar(nombre: String, puntaje: Int, comboMax: Int): Result<Unit> {
        return try {
            val uid = miUid() ?: return Result.failure(Exception("Usuario no autenticado"))
            val docRef = db.collection("ranking").document(uid)

            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val puntajeGuardado = snapshot.getLong("puntaje") ?: 0L
                if (puntaje <= puntajeGuardado) {
                    return Result.success(Unit)
                }
            }

            val datos = mapOf(
                "nombre" to nombre,
                "puntaje" to puntaje,
                "comboMax" to comboMax,
                "actualizado" to FieldValue.serverTimestamp()
            )

            docRef.set(datos).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun top(limite: Int): Result<List<EntradaRanking>> {
        return try {
            val querySnapshot = db.collection("ranking")
                .orderBy("puntaje", Query.Direction.DESCENDING)
                .limit(limite.toLong())
                .get()
                .await()

            val lista = querySnapshot.documents.mapNotNull { doc ->
                val nombre = doc.getString("nombre") ?: return@mapNotNull null
                val puntaje = doc.getLong("puntaje")?.toInt() ?: return@mapNotNull null
                val comboMax = doc.getLong("comboMax")?.toInt() ?: 0
                EntradaRanking(
                    uid = doc.id,
                    nombre = nombre,
                    puntaje = puntaje,
                    comboMax = comboMax
                )
            }
            Result.success(lista)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
