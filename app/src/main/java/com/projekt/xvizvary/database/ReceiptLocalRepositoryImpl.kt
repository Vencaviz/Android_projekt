package com.projekt.xvizvary.database

import com.projekt.xvizvary.database.model.Receipt
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReceiptLocalRepositoryImpl @Inject constructor(private val dao: ReceiptDao) : IReceiptLocalRepository {
    override suspend fun insert(place: Receipt) {
        return dao.insert(place)
    }

    override fun getAll(): Flow<List<Receipt>> {
        return dao.getAll()
    }

    override suspend fun getById(id: Long): Receipt {
        return dao.getById(id)
    }

    override suspend fun delete(place: Receipt) {
        dao.delete(place)
    }


}
