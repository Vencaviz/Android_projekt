package com.example.homework2.domain.repository

import com.example.homework2.domain.model.BudgetLimit
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observeBudgetLimits(month: String): Flow<List<BudgetLimit>>
    fun observeBudgetLimit(id: Long): Flow<BudgetLimit?>
    suspend fun upsert(limit: BudgetLimit): Long
    suspend fun deleteById(id: Long)
}

