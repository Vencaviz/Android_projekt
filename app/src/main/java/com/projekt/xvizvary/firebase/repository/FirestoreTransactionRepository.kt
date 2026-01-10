package com.projekt.xvizvary.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.projekt.xvizvary.firebase.FirestoreConstants
import com.projekt.xvizvary.firebase.model.FirestoreTransaction
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing transactions in Firestore
 */
@Singleton
class FirestoreTransactionRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun getUserTransactionsCollection(userId: String) =
        firestore
            .collection(FirestoreConstants.COLLECTION_USERS)
            .document(userId)
            .collection(FirestoreConstants.COLLECTION_TRANSACTIONS)

    /**
     * Get all transactions for a user as a Flow
     */
    fun getTransactions(userId: String): Flow<List<FirestoreTransaction>> = callbackFlow {
        val listener = getUserTransactionsCollection(userId)
            .orderBy(FirestoreConstants.FIELD_DATE, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val transactions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FirestoreTransaction::class.java)
                } ?: emptyList()

                trySend(transactions)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Get transactions by date range
     */
    fun getTransactionsByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<FirestoreTransaction>> = callbackFlow {
        val listener = getUserTransactionsCollection(userId)
            .whereGreaterThanOrEqualTo(FirestoreConstants.FIELD_DATE, startDate)
            .whereLessThanOrEqualTo(FirestoreConstants.FIELD_DATE, endDate)
            .orderBy(FirestoreConstants.FIELD_DATE, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val transactions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FirestoreTransaction::class.java)
                } ?: emptyList()

                trySend(transactions)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Add a new transaction
     */
    suspend fun addTransaction(userId: String, transaction: FirestoreTransaction): String? {
        return try {
            val docRef = getUserTransactionsCollection(userId).document()
            val transactionWithId = transaction.copy(id = docRef.id)
            docRef.set(transactionWithId).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Update a transaction
     */
    suspend fun updateTransaction(userId: String, transaction: FirestoreTransaction): Boolean {
        return try {
            getUserTransactionsCollection(userId)
                .document(transaction.id)
                .set(transaction)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Delete a transaction
     */
    suspend fun deleteTransaction(userId: String, transactionId: String): Boolean {
        return try {
            getUserTransactionsCollection(userId)
                .document(transactionId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get sum of transactions by type and date range
     * Note: Filters in-memory to avoid requiring Firestore composite indexes
     */
    suspend fun getSumByTypeAndDateRange(
        userId: String,
        type: String,
        startDate: Long,
        endDate: Long
    ): Double {
        return try {
            val snapshot = getUserTransactionsCollection(userId)
                .get()
                .await()

            snapshot.documents
                .mapNotNull { doc -> doc.toObject(FirestoreTransaction::class.java) }
                .filter { tx ->
                    tx.type == type && tx.date in startDate..endDate
                }
                .sumOf { it.amount }
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    /**
     * Get sum of transactions by category, type and date range
     */
    suspend fun getSumByCategoryAndDateRange(
        userId: String,
        categoryId: String,
        type: String,
        startDate: Long,
        endDate: Long
    ): Double {
        return try {
            val snapshot = getUserTransactionsCollection(userId)
                .get()
                .await()

            snapshot.documents
                .mapNotNull { doc -> doc.toObject(FirestoreTransaction::class.java) }
                .filter { tx ->
                    tx.categoryId == categoryId && 
                    tx.type == type && 
                    tx.date in startDate..endDate
                }
                .sumOf { it.amount }
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    /**
     * Get all transactions once (not as Flow) for calculations
     */
    suspend fun getTransactionsOnce(userId: String): List<FirestoreTransaction> {
        return try {
            val snapshot = getUserTransactionsCollection(userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(FirestoreTransaction::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
