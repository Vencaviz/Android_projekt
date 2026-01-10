package com.projekt.xvizvary.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.projekt.xvizvary.firebase.FirestoreConstants
import com.projekt.xvizvary.firebase.model.FirestoreLimit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing limits in Firestore
 */
@Singleton
class FirestoreLimitRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun getUserLimitsCollection(userId: String) =
        firestore
            .collection(FirestoreConstants.COLLECTION_USERS)
            .document(userId)
            .collection(FirestoreConstants.COLLECTION_LIMITS)

    /**
     * Get all limits for a user as a Flow
     */
    fun getLimits(userId: String): Flow<List<FirestoreLimit>> = callbackFlow {
        val listener = getUserLimitsCollection(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val limits = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FirestoreLimit::class.java)
                } ?: emptyList()

                trySend(limits)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Get limit by ID
     */
    suspend fun getLimitById(userId: String, limitId: String): FirestoreLimit? {
        return try {
            val doc = getUserLimitsCollection(userId)
                .document(limitId)
                .get()
                .await()
            doc.toObject(FirestoreLimit::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Add a new limit
     */
    suspend fun addLimit(userId: String, limit: FirestoreLimit): Result<String> {
        return try {
            val docRef = getUserLimitsCollection(userId).document()
            val limitWithId = limit.copy(id = docRef.id)
            docRef.set(limitWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update a limit
     */
    suspend fun updateLimit(userId: String, limit: FirestoreLimit): Result<Unit> {
        return try {
            getUserLimitsCollection(userId)
                .document(limit.id)
                .set(limit)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete a limit
     */
    suspend fun deleteLimit(userId: String, limitId: String): Result<Unit> {
        return try {
            getUserLimitsCollection(userId)
                .document(limitId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
