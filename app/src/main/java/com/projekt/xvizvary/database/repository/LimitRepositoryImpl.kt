package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.LimitDao
import com.projekt.xvizvary.database.TransactionDao
import com.projekt.xvizvary.database.model.Limit
import com.projekt.xvizvary.database.model.LimitWithCategory
import com.projekt.xvizvary.database.model.LimitWithSpent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LimitRepositoryImpl @Inject constructor(
    private val limitDao: LimitDao,
    private val transactionDao: TransactionDao
) : LimitRepository {

    override fun getAllLimits(): Flow<List<Limit>> {
        return limitDao.getAll()
    }

    override fun getAllLimitsWithCategory(): Flow<List<LimitWithCategory>> {
        return limitDao.getAllWithCategory()
    }

    override suspend fun getLimitById(id: Long): Limit? {
        return limitDao.getById(id)
    }

    override suspend fun getLimitByIdWithCategory(id: Long): LimitWithCategory? {
        return limitDao.getByIdWithCategory(id)
    }

    override suspend fun getLimitByCategoryId(categoryId: Long): Limit? {
        return limitDao.getByCategoryId(categoryId)
    }

    override suspend fun insertLimit(limit: Limit): Long {
        return limitDao.insert(limit)
    }

    override suspend fun updateLimit(limit: Limit) {
        limitDao.update(limit)
    }

    override suspend fun deleteLimit(limit: Limit) {
        limitDao.delete(limit)
    }

    override suspend fun deleteAllLimits() {
        limitDao.deleteAll()
    }

    override suspend fun getLimitsWithSpent(startDate: Long, endDate: Long): List<LimitWithSpent> {
        val limitsWithCategory = limitDao.getAllWithCategory().first()

        return limitsWithCategory.map { limitWithCategory ->
            val spentAmount = transactionDao.getSpentByCategoryAndDateRange(
                categoryId = limitWithCategory.limit.categoryId,
                startDate = startDate,
                endDate = endDate
            ) ?: 0.0

            LimitWithSpent(
                limit = limitWithCategory.limit,
                category = limitWithCategory.category,
                spentAmount = spentAmount
            )
        }
    }
}
