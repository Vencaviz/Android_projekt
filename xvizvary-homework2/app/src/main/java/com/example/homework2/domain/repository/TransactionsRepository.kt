package com.example.homework2.domain.repository

import com.example.homework2.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun observeTransactions(): Flow<List<Transaction>>
    fun observeTransaction(id: Long): Flow<Transaction?>
    suspend fun upsert(transaction: Transaction): Long
    suspend fun deleteById(id: Long)
}

