package com.projekt.xvizvary.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.projekt.xvizvary.database.model.Limit
import com.projekt.xvizvary.database.model.LimitWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface LimitDao {

    @Insert
    suspend fun insert(limit: Limit): Long

    @Update
    suspend fun update(limit: Limit)

    @Delete
    suspend fun delete(limit: Limit)

    @Query("SELECT * FROM limits ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Limit>>

    @Transaction
    @Query("SELECT * FROM limits ORDER BY createdAt DESC")
    fun getAllWithCategory(): Flow<List<LimitWithCategory>>

    @Query("SELECT * FROM limits WHERE id = :id")
    suspend fun getById(id: Long): Limit?

    @Transaction
    @Query("SELECT * FROM limits WHERE id = :id")
    suspend fun getByIdWithCategory(id: Long): LimitWithCategory?

    @Query("SELECT * FROM limits WHERE categoryId = :categoryId LIMIT 1")
    suspend fun getByCategoryId(categoryId: Long): Limit?

    @Query("DELETE FROM limits")
    suspend fun deleteAll()
}
