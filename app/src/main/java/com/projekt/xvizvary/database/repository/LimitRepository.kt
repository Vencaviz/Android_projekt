package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.model.Limit
import kotlinx.coroutines.flow.Flow

interface LimitRepository {
    fun getLimitsByUser(userId: String): Flow<List<Limit>>
    suspend fun getLimitsByUserOnce(userId: String): List<Limit>
    suspend fun getLimitById(id: Long): Limit?
    suspend fun getLimitByFirestoreId(firestoreId: String): Limit?
    suspend fun getLimitByUserAndCategoryId(userId: String, categoryId: String): Limit?
    suspend fun insertLimit(limit: Limit): Long
    suspend fun insertLimits(limits: List<Limit>)
    suspend fun updateLimit(limit: Limit)
    suspend fun deleteLimit(limit: Limit)
    suspend fun deleteLimitByFirestoreId(firestoreId: String)
    suspend fun deleteAllByUser(userId: String)
}
