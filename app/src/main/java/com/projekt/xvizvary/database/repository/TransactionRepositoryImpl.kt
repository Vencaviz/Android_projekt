package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.TransactionDao
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.model.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAll()
    }

    override fun getAllTransactionsWithCategory(): Flow<List<TransactionWithCategory>> {
        return transactionDao.getAllWithCategory()
    }

    override fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionDao.getByDateRange(startDate, endDate)
    }

    override fun getTransactionsByDateRangeWithCategory(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionWithCategory>> {
        return transactionDao.getByDateRangeWithCategory(startDate, endDate)
    }

    override fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> {
        return transactionDao.getByCategory(categoryId)
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getById(id)
    }

    override suspend fun getTransactionByIdWithCategory(id: Long): TransactionWithCategory? {
        return transactionDao.getByIdWithCategory(id)
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insert(transaction)
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    override suspend fun getSumByTypeAndDateRange(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Double {
        return transactionDao.getSumByTypeAndDateRange(type, startDate, endDate) ?: 0.0
    }

    override suspend fun getSpentByCategoryAndDateRange(
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ): Double {
        return transactionDao.getSpentByCategoryAndDateRange(categoryId, startDate, endDate) ?: 0.0
    }

    override suspend fun deleteAllTransactions() {
        transactionDao.deleteAll()
    }
}
