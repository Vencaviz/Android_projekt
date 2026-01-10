package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.model.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getAllTransactions(): Flow<List<Transaction>>

    fun getAllTransactionsWithCategory(): Flow<List<TransactionWithCategory>>

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>

    fun getTransactionsByDateRangeWithCategory(startDate: Long, endDate: Long): Flow<List<TransactionWithCategory>>

    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>

    suspend fun getTransactionById(id: Long): Transaction?

    suspend fun getTransactionByIdWithCategory(id: Long): TransactionWithCategory?

    suspend fun insertTransaction(transaction: Transaction): Long

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun getSumByTypeAndDateRange(type: TransactionType, startDate: Long, endDate: Long): Double

    suspend fun getSpentByCategoryAndDateRange(categoryId: Long, startDate: Long, endDate: Long): Double

    suspend fun deleteAllTransactions()
}
