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
    suspend fun insert(place: Receipt)

    @Query("SELECT * FROM receipts")
    fun getAll(): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE id = :id")
    suspend fun getById(id: Long): Receipt

    @Delete
    suspend fun delete(place:Receipt)
}
