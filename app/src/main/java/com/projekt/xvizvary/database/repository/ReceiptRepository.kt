package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.model.Receipt
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {

    fun getAllReceipts(): Flow<List<Receipt>>

    fun getUnlinkedReceipts(): Flow<List<Receipt>>

    suspend fun getReceiptById(id: Long): Receipt?

    suspend fun getReceiptByTransactionId(transactionId: Long): Receipt?

    suspend fun insertReceipt(receipt: Receipt): Long

    suspend fun updateReceipt(receipt: Receipt)

    suspend fun deleteReceipt(receipt: Receipt)

    suspend fun linkReceiptToTransaction(receiptId: Long, transactionId: Long)

    suspend fun deleteAllReceipts()
}
