package com.projekt.xvizvary.sync

import android.util.Log
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Limit
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.repository.CategoryRepository
import com.projekt.xvizvary.database.repository.LimitRepository
import com.projekt.xvizvary.database.repository.TransactionRepository
import com.projekt.xvizvary.firebase.model.FirestoreCategory
import com.projekt.xvizvary.firebase.model.FirestoreLimit
import com.projekt.xvizvary.firebase.model.FirestoreTransaction
import com.projekt.xvizvary.firebase.repository.FirestoreCategoryRepository
import com.projekt.xvizvary.firebase.repository.FirestoreLimitRepository
import com.projekt.xvizvary.firebase.repository.FirestoreTransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for synchronizing data between Firestore (cloud) and Room (local).
 * 
 * Strategy:
 * 1. On login/sync: Download all data from Firestore -> Save to Room
 * 2. On data changes: Save to Room first, then sync to Firestore
 * 3. UI always reads from Room (offline-first)
 */
@Singleton
class SyncRepository @Inject constructor(
    private val firestoreTransactionRepository: FirestoreTransactionRepository,
    private val firestoreCategoryRepository: FirestoreCategoryRepository,
    private val firestoreLimitRepository: FirestoreLimitRepository,
    private val localTransactionRepository: TransactionRepository,
    private val localCategoryRepository: CategoryRepository,
    private val localLimitRepository: LimitRepository
) {
    companion object {
        private const val TAG = "SyncRepository"
    }

    /**
     * Syncs all data from Firestore to local Room database.
     * Called after login.
     */
    suspend fun syncFromCloud(userId: String): SyncResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting sync from cloud for user: $userId")
            
            // Sync categories first (transactions depend on them)
            syncCategoriesFromCloud(userId)
            
            // Sync transactions
            syncTransactionsFromCloud(userId)
            
            // Sync limits
            syncLimitsFromCloud(userId)
            
            Log.d(TAG, "Sync from cloud completed successfully")
            SyncResult.Success
        } catch (e: Exception) {
            Log.e(TAG, "Sync from cloud failed", e)
            SyncResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Clears all local data for a user (on logout).
     */
    suspend fun clearLocalData(userId: String) = withContext(Dispatchers.IO) {
        try {
            localTransactionRepository.deleteAllByUser(userId)
            localCategoryRepository.deleteAllByUser(userId)
            localLimitRepository.deleteAllByUser(userId)
            Log.d(TAG, "Local data cleared for user: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear local data", e)
        }
    }

    // ==================== Categories ====================

    private suspend fun syncCategoriesFromCloud(userId: String) {
        val cloudCategories = firestoreCategoryRepository.getCategoriesOnce(userId)
        
        val localCategories = cloudCategories.map { firestoreCategory ->
            Category(
                firestoreId = firestoreCategory.id,
                userId = userId,
                name = firestoreCategory.name,
                icon = firestoreCategory.icon,
                color = firestoreCategory.color,
                isDefault = firestoreCategory.isDefault
            )
        }
        
        localCategoryRepository.insertCategories(localCategories)
        Log.d(TAG, "Synced ${localCategories.size} categories from cloud")
    }

    suspend fun addCategory(userId: String, category: Category): String? = withContext(Dispatchers.IO) {
        try {
            // Add to Firestore first
            val firestoreCategory = FirestoreCategory(
                name = category.name,
                icon = category.icon,
                color = category.color,
                isDefault = category.isDefault
            )
            val firestoreId = firestoreCategoryRepository.addCategory(userId, firestoreCategory)
            
            if (firestoreId != null) {
                // Add to local with Firestore ID
                val localCategory = category.copy(
                    firestoreId = firestoreId,
                    userId = userId
                )
                localCategoryRepository.insertCategory(localCategory)
            }
            
            firestoreId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add category", e)
            null
        }
    }

    // ==================== Transactions ====================

    private suspend fun syncTransactionsFromCloud(userId: String) {
        val cloudTransactions = firestoreTransactionRepository.getTransactionsOnce(userId)
        
        val localTransactions = cloudTransactions.map { firestoreTx ->
            Transaction(
                firestoreId = firestoreTx.id,
                userId = userId,
                name = firestoreTx.name,
                amount = firestoreTx.amount,
                type = TransactionType.valueOf(firestoreTx.type),
                categoryId = firestoreTx.categoryId,
                date = firestoreTx.date,
                note = firestoreTx.note,
                createdAt = firestoreTx.createdAt,
                isSynced = true
            )
        }
        
        localTransactionRepository.insertTransactions(localTransactions)
        Log.d(TAG, "Synced ${localTransactions.size} transactions from cloud")
    }

    suspend fun addTransaction(userId: String, transaction: Transaction): String? = withContext(Dispatchers.IO) {
        try {
            // Add to Firestore first
            val firestoreTx = FirestoreTransaction(
                name = transaction.name,
                amount = transaction.amount,
                type = transaction.type.name,
                categoryId = transaction.categoryId ?: "",
                date = transaction.date,
                note = transaction.note,
                createdAt = transaction.createdAt
            )
            val firestoreId = firestoreTransactionRepository.addTransaction(userId, firestoreTx)
            
            if (firestoreId != null) {
                // Add to local with Firestore ID
                val localTx = transaction.copy(
                    firestoreId = firestoreId,
                    userId = userId,
                    isSynced = true
                )
                localTransactionRepository.insertTransaction(localTx)
            }
            
            firestoreId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add transaction", e)
            null
        }
    }

    suspend fun deleteTransaction(userId: String, firestoreId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Delete from Firestore
            val success = firestoreTransactionRepository.deleteTransaction(userId, firestoreId)
            
            if (success) {
                // Delete from local
                localTransactionRepository.deleteTransactionByFirestoreId(firestoreId)
            }
            
            success
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete transaction", e)
            false
        }
    }

    // ==================== Limits ====================

    private suspend fun syncLimitsFromCloud(userId: String) {
        val cloudLimits = firestoreLimitRepository.getLimitsOnce(userId)
        
        val localLimits = cloudLimits.map { firestoreLimit ->
            Limit(
                firestoreId = firestoreLimit.id,
                userId = userId,
                categoryId = firestoreLimit.categoryId,
                limitAmount = firestoreLimit.limitAmount,
                periodMonths = firestoreLimit.periodMonths,
                createdAt = firestoreLimit.createdAt
            )
        }
        
        localLimitRepository.insertLimits(localLimits)
        Log.d(TAG, "Synced ${localLimits.size} limits from cloud")
    }

    suspend fun addLimit(userId: String, limit: Limit): String? = withContext(Dispatchers.IO) {
        try {
            // Add to Firestore first
            val firestoreLimit = FirestoreLimit(
                categoryId = limit.categoryId,
                limitAmount = limit.limitAmount,
                periodMonths = limit.periodMonths,
                createdAt = limit.createdAt
            )
            val firestoreId = firestoreLimitRepository.addLimit(userId, firestoreLimit)
            
            if (firestoreId != null) {
                // Add to local with Firestore ID
                val localLimit = limit.copy(
                    firestoreId = firestoreId,
                    userId = userId
                )
                localLimitRepository.insertLimit(localLimit)
            }
            
            firestoreId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add limit", e)
            null
        }
    }

    suspend fun updateLimit(userId: String, limit: Limit): Boolean = withContext(Dispatchers.IO) {
        try {
            // Update in Firestore
            val firestoreLimit = FirestoreLimit(
                id = limit.firestoreId,
                categoryId = limit.categoryId,
                limitAmount = limit.limitAmount,
                periodMonths = limit.periodMonths,
                createdAt = limit.createdAt
            )
            val success = firestoreLimitRepository.updateLimit(userId, firestoreLimit)
            
            if (success) {
                // Update local
                localLimitRepository.updateLimit(limit)
            }
            
            success
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update limit", e)
            false
        }
    }

    suspend fun deleteLimit(userId: String, firestoreId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Delete from Firestore
            val success = firestoreLimitRepository.deleteLimit(userId, firestoreId)
            
            if (success) {
                // Delete from local
                localLimitRepository.deleteLimitByFirestoreId(firestoreId)
            }
            
            success
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete limit", e)
            false
        }
    }
}

sealed class SyncResult {
    data object Success : SyncResult()
    data class Error(val message: String) : SyncResult()
}
