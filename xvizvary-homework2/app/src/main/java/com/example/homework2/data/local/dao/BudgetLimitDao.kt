package com.example.homework2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.homework2.data.local.entity.BudgetLimitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetLimitDao {

    @Query("SELECT * FROM budget_limits WHERE month = :month ORDER BY category ASC")
    fun observeForMonth(month: String): Flow<List<BudgetLimitEntity>>

    @Query("SELECT * FROM budget_limits WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<BudgetLimitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BudgetLimitEntity): Long

    @Query("DELETE FROM budget_limits WHERE id = :id")
    suspend fun deleteById(id: Long)
}

