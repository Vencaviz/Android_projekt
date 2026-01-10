package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.model.Limit
import com.projekt.xvizvary.database.model.LimitWithCategory
import com.projekt.xvizvary.database.model.LimitWithSpent
import kotlinx.coroutines.flow.Flow

interface LimitRepository {

    fun getAllLimits(): Flow<List<Limit>>

    fun getAllLimitsWithCategory(): Flow<List<LimitWithCategory>>

    suspend fun getLimitById(id: Long): Limit?

    suspend fun getLimitByIdWithCategory(id: Long): LimitWithCategory?

    suspend fun getLimitByCategoryId(categoryId: Long): Limit?

    suspend fun insertLimit(limit: Limit): Long

    suspend fun updateLimit(limit: Limit)

    suspend fun deleteLimit(limit: Limit)

    suspend fun deleteAllLimits()

    /**
     * Gets all limits with their current spending amounts for the given date range.
     */
    suspend fun getLimitsWithSpent(startDate: Long, endDate: Long): List<LimitWithSpent>
}
