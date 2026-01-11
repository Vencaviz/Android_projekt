package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactionsByUser(userId: String): Flow<List<Transaction>>
    suspend fun getTransactionsByUserOnce(userId: String): List<Transaction>
    fun getTransactionsByUserAndDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<Transaction>>
    fun getTransactionsByUserAndCategory(userId: String, categoryId: String): Flow<List<Transaction>>
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun getTransactionByFirestoreId(firestoreId: String): Transaction?
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun insertTransactions(transactions: List<Transaction>)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun deleteTransactionByFirestoreId(firestoreId: String)
    suspend fun getSumByUserTypeAndDateRange(userId: String, type: TransactionType, startDate: Long, endDate: Long): Double
    suspend fun getSpentByUserCategoryAndDateRange(userId: String, categoryId: String, startDate: Long, endDate: Long): Double
    suspend fun deleteAllByUser(userId: String)
}
