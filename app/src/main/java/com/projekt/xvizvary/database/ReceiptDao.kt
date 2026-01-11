package com.projekt.xvizvary.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.projekt.xvizvary.database.model.Receipt
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {

    @Insert
    suspend fun insert(receipt: Receipt): Long

    @Update
    suspend fun update(receipt: Receipt)

    @Delete
    suspend fun delete(receipt: Receipt)

    @Query("SELECT * FROM receipts ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE id = :id")
    suspend fun getById(id: Long): Receipt?

    @Query("SELECT * FROM receipts WHERE transactionId = :transactionId")
    suspend fun getByTransactionId(transactionId: Long): Receipt?

    @Query("SELECT * FROM receipts WHERE transactionId IS NULL ORDER BY createdAt DESC")
    fun getUnlinkedReceipts(): Flow<List<Receipt>>

    @Query("UPDATE receipts SET transactionId = :transactionId WHERE id = :receiptId")
    suspend fun linkToTransaction(receiptId: Long, transactionId: Long)

    @Query("DELETE FROM receipts")
    suspend fun deleteAll()
}
