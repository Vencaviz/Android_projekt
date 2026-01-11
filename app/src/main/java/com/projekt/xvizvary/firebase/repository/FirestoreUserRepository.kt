package com.projekt.xvizvary.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.projekt.xvizvary.firebase.FirestoreConstants
import com.projekt.xvizvary.firebase.model.FirestoreCategory
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing user documents in Firestore
 */
@Singleton
class FirestoreUserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    /**
     * Creates a new user document with default data after registration
     */
    suspend fun createUserDocument(
        userId: String,
        email: String,
        displayName: String? = null
    ): Result<Unit> {
        return try {
            val userRef = firestore
                .collection(FirestoreConstants.COLLECTION_USERS)
                .document(userId)

            // Create user profile document
            val userData = hashMapOf(
                FirestoreConstants.FIELD_EMAIL to email,
                FirestoreConstants.FIELD_DISPLAY_NAME to (displayName ?: email.substringBefore("@")),
                FirestoreConstants.FIELD_CREATED_AT to System.currentTimeMillis()
            )
            
            userRef.set(userData).await()

            // Create default categories for the user
            createDefaultCategories(userId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates default categories for a new user
     */
    private suspend fun createDefaultCategories(userId: String) {
        val categoriesRef = firestore
            .collection(FirestoreConstants.COLLECTION_USERS)
            .document(userId)
            .collection(FirestoreConstants.COLLECTION_CATEGORIES)

        val defaultCategories = FirestoreCategory.getDefaultCategories()

        defaultCategories.forEach { category ->
            categoriesRef.document(category.id).set(
                hashMapOf(
                    FirestoreConstants.FIELD_NAME to category.name,
                    FirestoreConstants.FIELD_ICON to category.icon,
                    FirestoreConstants.FIELD_COLOR to category.color,
                    FirestoreConstants.FIELD_IS_DEFAULT to category.isDefault,
                    FirestoreConstants.FIELD_CREATED_AT to System.currentTimeMillis()
                )
            ).await()
        }
    }

    /**
     * Checks if user document exists
     */
    suspend fun userDocumentExists(userId: String): Boolean {
        return try {
            val doc = firestore
                .collection(FirestoreConstants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Deletes all user data (for account deletion)
     */
    suspend fun deleteUserData(userId: String): Result<Unit> {
        return try {
            val userRef = firestore
                .collection(FirestoreConstants.COLLECTION_USERS)
                .document(userId)

            // Delete subcollections
            deleteCollection(userRef.collection(FirestoreConstants.COLLECTION_TRANSACTIONS))
            deleteCollection(userRef.collection(FirestoreConstants.COLLECTION_CATEGORIES))
            deleteCollection(userRef.collection(FirestoreConstants.COLLECTION_LIMITS))
            deleteCollection(userRef.collection(FirestoreConstants.COLLECTION_RECEIPTS))

            // Delete user document
            userRef.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun deleteCollection(collection: com.google.firebase.firestore.CollectionReference) {
        val documents = collection.get().await()
        documents.forEach { doc ->
            doc.reference.delete().await()
        }
    }
}
