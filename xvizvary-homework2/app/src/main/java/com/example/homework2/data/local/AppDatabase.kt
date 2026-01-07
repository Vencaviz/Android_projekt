package com.example.homework2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.homework2.data.local.dao.BudgetLimitDao
import com.example.homework2.data.local.dao.TransactionsDao
import com.example.homework2.data.local.entity.BudgetLimitEntity
import com.example.homework2.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        BudgetLimitEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionsDao
    abstract fun budgetLimitDao(): BudgetLimitDao
}

