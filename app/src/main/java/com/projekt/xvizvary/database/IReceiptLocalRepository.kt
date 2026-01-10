package com.projekt.xvizvary.database

import com.projekt.xvizvary.database.model.Receipt
import kotlinx.coroutines.flow.Flow

interface IReceiptLocalRepository {

    suspend fun insert(place: Receipt)
    fun getAll(): Flow<List<Receipt>>
    suspend fun getById(id:Long): Receipt
    suspend fun delete(place:Receipt)

}
