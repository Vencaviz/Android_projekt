package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.TransactionDao
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getTransactionsByUser(userId: String): Flow<List<Transaction>> {
        return transactionDao.getAllByUser(userId)
    }

    override suspend fun getTransactionsByUserOnce(userId: String): List<Transaction> {
        return transactionDao.getAllByUserOnce(userId)
    }

    override fun getTransactionsByUserAndDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>> {
        return transactionDao.getByUserAndDateRange(userId, startDate, endDate)
    }

    override fun getTransactionsByUserAndCategory(
        userId: String,
        categoryId: String
    ): Flow<List<Transaction>> {
        return transactionDao.getByUserAndCategory(userId, categoryId)
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getById(id)
    }

    override suspend fun getTransactionByFirestoreId(firestoreId: String): Transaction? {
        return transactionDao.getByFirestoreId(firestoreId)
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insert(transaction)
    }

    override suspend fun insertTransactions(transactions: List<Transaction>) {
        transactionDao.insertAll(transactions)
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    override suspend fun deleteTransactionByFirestoreId(firestoreId: String) {
        transactionDao.deleteByFirestoreId(firestoreId)
    }

    override suspend fun getSumByUserTypeAndDateRange(
        userId: String,
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Double {
        return transactionDao.getSumByUserTypeAndDateRange(userId, type, startDate, endDate) ?: 0.0
    }

    override suspend fun getSpentByUserCategoryAndDateRange(
        userId: String,
        categoryId: String,
        startDate: Long,
        endDate: Long
    ): Double {
        return transactionDao.getSpentByUserCategoryAndDateRange(userId, categoryId, startDate, endDate) ?: 0.0
    }

    override suspend fun deleteAllByUser(userId: String) {
        transactionDao.deleteAllByUser(userId)
    }
}
