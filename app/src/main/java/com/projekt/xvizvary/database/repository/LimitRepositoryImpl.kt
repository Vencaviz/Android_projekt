package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.LimitDao
import com.projekt.xvizvary.database.model.Limit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LimitRepositoryImpl @Inject constructor(
    private val limitDao: LimitDao
) : LimitRepository {

    override fun getLimitsByUser(userId: String): Flow<List<Limit>> {
        return limitDao.getAllByUser(userId)
    }

    override suspend fun getLimitsByUserOnce(userId: String): List<Limit> {
        return limitDao.getAllByUserOnce(userId)
    }

    override suspend fun getLimitById(id: Long): Limit? {
        return limitDao.getById(id)
    }

    override suspend fun getLimitByFirestoreId(firestoreId: String): Limit? {
        return limitDao.getByFirestoreId(firestoreId)
    }

    override suspend fun getLimitByUserAndCategoryId(userId: String, categoryId: String): Limit? {
        return limitDao.getByUserAndCategoryId(userId, categoryId)
    }

    override suspend fun insertLimit(limit: Limit): Long {
        return limitDao.insert(limit)
    }

    override suspend fun insertLimits(limits: List<Limit>) {
        limitDao.insertAll(limits)
    }

    override suspend fun updateLimit(limit: Limit) {
        limitDao.update(limit)
    }

    override suspend fun deleteLimit(limit: Limit) {
        limitDao.delete(limit)
    }

    override suspend fun deleteLimitByFirestoreId(firestoreId: String) {
        limitDao.deleteByFirestoreId(firestoreId)
    }

    override suspend fun deleteAllByUser(userId: String) {
        limitDao.deleteAllByUser(userId)
    }
}
