package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.ReceiptDao
import com.projekt.xvizvary.database.model.Receipt
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptRepositoryImpl @Inject constructor(
    private val receiptDao: ReceiptDao
) : ReceiptRepository {

    override fun getAllReceipts(): Flow<List<Receipt>> {
        return receiptDao.getAll()
    }

    override fun getUnlinkedReceipts(): Flow<List<Receipt>> {
        return receiptDao.getUnlinkedReceipts()
    }

    override suspend fun getReceiptById(id: Long): Receipt? {
        return receiptDao.getById(id)
    }

    override suspend fun getReceiptByTransactionId(transactionId: Long): Receipt? {
        return receiptDao.getByTransactionId(transactionId)
    }

    override suspend fun insertReceipt(receipt: Receipt): Long {
        return receiptDao.insert(receipt)
    }

    override suspend fun updateReceipt(receipt: Receipt) {
        receiptDao.update(receipt)
    }

    override suspend fun deleteReceipt(receipt: Receipt) {
        receiptDao.delete(receipt)
    }

    override suspend fun linkReceiptToTransaction(receiptId: Long, transactionId: Long) {
        receiptDao.linkToTransaction(receiptId, transactionId)
    }

    override suspend fun deleteAllReceipts() {
        receiptDao.deleteAll()
    }
}
