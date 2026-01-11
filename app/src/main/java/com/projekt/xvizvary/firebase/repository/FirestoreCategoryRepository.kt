package com.projekt.xvizvary.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.projekt.xvizvary.firebase.FirestoreConstants
import com.projekt.xvizvary.firebase.model.FirestoreCategory
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing categories in Firestore
 */
@Singleton
class FirestoreCategoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun getUserCategoriesCollection(userId: String) =
        firestore
            .collection(FirestoreConstants.COLLECTION_USERS)
            .document(userId)
            .collection(FirestoreConstants.COLLECTION_CATEGORIES)

    /**
     * Get all categories for a user as a Flow
     */
    fun getCategories(userId: String): Flow<List<FirestoreCategory>> = callbackFlow {
        val listener = getUserCategoriesCollection(userId)
            .orderBy(FirestoreConstants.FIELD_NAME)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val categories = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FirestoreCategory::class.java)
                } ?: emptyList()

                trySend(categories)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Get all categories once (not as Flow)
     */
    suspend fun getCategoriesOnce(userId: String): List<FirestoreCategory> {
        return try {
            val snapshot = getUserCategoriesCollection(userId)
                .orderBy(FirestoreConstants.FIELD_NAME)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(FirestoreCategory::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Get category by ID
     */
    suspend fun getCategoryById(userId: String, categoryId: String): FirestoreCategory? {
        return try {
            val doc = getUserCategoriesCollection(userId)
                .document(categoryId)
                .get()
                .await()
            doc.toObject(FirestoreCategory::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Add a new category
     */
    suspend fun addCategory(userId: String, category: FirestoreCategory): String? {
        return try {
            val docRef = getUserCategoriesCollection(userId).document()
            val categoryWithId = category.copy(id = docRef.id)
            docRef.set(categoryWithId).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Delete a category
     */
    suspend fun deleteCategory(userId: String, categoryId: String): Result<Unit> {
        return try {
            getUserCategoriesCollection(userId)
                .document(categoryId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
