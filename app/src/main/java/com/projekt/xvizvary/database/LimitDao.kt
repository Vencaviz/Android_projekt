package com.projekt.xvizvary.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.projekt.xvizvary.database.model.Limit
import kotlinx.coroutines.flow.Flow

@Dao
interface LimitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(limit: Limit): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(limits: List<Limit>)

    @Update
    suspend fun update(limit: Limit)

    @Delete
    suspend fun delete(limit: Limit)

    @Query("SELECT * FROM limits WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllByUser(userId: String): Flow<List<Limit>>

    @Query("SELECT * FROM limits WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getAllByUserOnce(userId: String): List<Limit>

    @Query("SELECT * FROM limits WHERE id = :id")
    suspend fun getById(id: Long): Limit?

    @Query("SELECT * FROM limits WHERE firestoreId = :firestoreId")
    suspend fun getByFirestoreId(firestoreId: String): Limit?

    @Query("SELECT * FROM limits WHERE userId = :userId AND categoryId = :categoryId LIMIT 1")
    suspend fun getByUserAndCategoryId(userId: String, categoryId: String): Limit?

    @Query("DELETE FROM limits WHERE firestoreId = :firestoreId")
    suspend fun deleteByFirestoreId(firestoreId: String)

    @Query("DELETE FROM limits WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: String)

    @Query("DELETE FROM limits")
    suspend fun deleteAll()
}
