package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.TransactionDao
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.model.TransactionWithCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any

class TransactionRepositoryTest {

    private lateinit var transactionDao: TransactionDao
    private lateinit var repository: TransactionRepositoryImpl

    private val testTransaction = Transaction(
        id = 1,
        name = "Test Transaction",
        amount = 100.0,
        type = TransactionType.EXPENSE,
        categoryId = null,
        date = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        transactionDao = mock()
        repository = TransactionRepositoryImpl(transactionDao)
    }

    @Test
    fun `getAllTransactions returns flow from dao`() = runTest {
        val transactions = listOf(testTransaction)
        whenever(transactionDao.getAll()).thenReturn(flowOf(transactions))

        val result = repository.getAllTransactions().first()

        assertEquals(1, result.size)
        assertEquals("Test Transaction", result[0].name)
    }

    @Test
    fun `insertTransaction calls dao insert`() = runTest {
        whenever(transactionDao.insert(testTransaction)).thenReturn(1L)

        val result = repository.insertTransaction(testTransaction)

        assertEquals(1L, result)
        verify(transactionDao).insert(testTransaction)
    }

    @Test
    fun `deleteTransaction calls dao delete`() = runTest {
        repository.deleteTransaction(testTransaction)

        verify(transactionDao).delete(testTransaction)
    }

    @Test
    fun `getTransactionById returns transaction from dao`() = runTest {
        whenever(transactionDao.getById(1L)).thenReturn(testTransaction)

        val result = repository.getTransactionById(1L)

        assertNotNull(result)
        assertEquals("Test Transaction", result?.name)
    }

    @Test
    fun `getTransactionById returns null for non-existent id`() = runTest {
        whenever(transactionDao.getById(999L)).thenReturn(null)

        val result = repository.getTransactionById(999L)

        assertNull(result)
    }

    @Test
    fun `getSumByTypeAndDateRange returns sum from dao`() = runTest {
        whenever(transactionDao.getSumByTypeAndDateRange(any(), any(), any()))
            .thenReturn(500.0)

        val result = repository.getSumByTypeAndDateRange(
            TransactionType.EXPENSE,
            0L,
            System.currentTimeMillis()
        )

        assertEquals(500.0, result, 0.01)
    }

    @Test
    fun `getSumByTypeAndDateRange returns zero when dao returns null`() = runTest {
        whenever(transactionDao.getSumByTypeAndDateRange(any(), any(), any()))
            .thenReturn(null)

        val result = repository.getSumByTypeAndDateRange(
            TransactionType.EXPENSE,
            0L,
            System.currentTimeMillis()
        )

        assertEquals(0.0, result, 0.01)
    }

    @Test
    fun `updateTransaction calls dao update`() = runTest {
        repository.updateTransaction(testTransaction)

        verify(transactionDao).update(testTransaction)
    }
}
