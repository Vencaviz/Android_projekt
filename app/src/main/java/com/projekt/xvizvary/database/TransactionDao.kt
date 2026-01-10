package com.projekt.xvizvary.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction as RoomTransaction
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.model.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<Transaction>>

    @RoomTransaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllWithCategory(): Flow<List<TransactionWithCategory>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): Transaction?

    @RoomTransaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getByIdWithCategory(id: Long): TransactionWithCategory?

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>

    @RoomTransaction
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByDateRangeWithCategory(startDate: Long, endDate: Long): Flow<List<TransactionWithCategory>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getByCategory(categoryId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByCategoryAndDateRange(categoryId: Long, startDate: Long, endDate: Long): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    suspend fun getSumByTypeAndDateRange(type: TransactionType, startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE categoryId = :categoryId AND type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate")
    suspend fun getSpentByCategoryAndDateRange(categoryId: Long, startDate: Long, endDate: Long): Double?

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}
