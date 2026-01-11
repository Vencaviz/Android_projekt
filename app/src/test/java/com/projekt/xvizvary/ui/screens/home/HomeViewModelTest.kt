package com.projekt.xvizvary.ui.screens.home

import com.projekt.xvizvary.auth.repository.UserRepository
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.repository.CategoryRepository
import com.projekt.xvizvary.database.repository.TransactionRepository
import com.projekt.xvizvary.sync.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userRepository: UserRepository
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var syncRepository: SyncRepository

    private val testUserId = "testUser123"

    private val testCategory = Category(
        id = 1,
        firestoreId = "cat1",
        userId = testUserId,
        name = "Food",
        icon = "restaurant",
        color = 0xFFE57373
    )

    private val testTransactions = listOf(
        Transaction(
            id = 1,
            firestoreId = "tx1",
            userId = testUserId,
            name = "Grocery",
            amount = 500.0,
            type = TransactionType.EXPENSE,
            categoryId = "cat1",
            date = System.currentTimeMillis()
        ),
        Transaction(
            id = 2,
            firestoreId = "tx2",
            userId = testUserId,
            name = "Salary",
            amount = 25000.0,
            type = TransactionType.INCOME,
            categoryId = null,
            date = System.currentTimeMillis()
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mock()
        transactionRepository = mock()
        categoryRepository = mock()
        syncRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        whenever(userRepository.getCurrentUserId()).thenReturn(testUserId)
        whenever(categoryRepository.getCategoriesByUserOnce(testUserId))
            .thenReturn(listOf(testCategory))
        whenever(transactionRepository.getTransactionsByUserAndDateRange(any(), any(), any()))
            .thenReturn(flowOf(emptyList()))
        whenever(transactionRepository.getSumByUserTypeAndDateRange(any(), any(), any(), any()))
            .thenReturn(0.0)

        val viewModel = HomeViewModel(userRepository, transactionRepository, categoryRepository, syncRepository)

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `transactions are loaded correctly`() = runTest {
        whenever(userRepository.getCurrentUserId()).thenReturn(testUserId)
        whenever(categoryRepository.getCategoriesByUserOnce(testUserId))
            .thenReturn(listOf(testCategory))
        whenever(transactionRepository.getTransactionsByUserAndDateRange(any(), any(), any()))
            .thenReturn(flowOf(testTransactions))
        whenever(transactionRepository.getSumByUserTypeAndDateRange(any(), any(), any(), any()))
            .thenReturn(0.0)

        val viewModel = HomeViewModel(userRepository, transactionRepository, categoryRepository, syncRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.transactions.size)
    }

    @Test
    fun `monthly balance is calculated correctly`() = runTest {
        whenever(userRepository.getCurrentUserId()).thenReturn(testUserId)
        whenever(categoryRepository.getCategoriesByUserOnce(testUserId))
            .thenReturn(listOf(testCategory))
        whenever(transactionRepository.getTransactionsByUserAndDateRange(any(), any(), any()))
            .thenReturn(flowOf(testTransactions))
        whenever(transactionRepository.getSumByUserTypeAndDateRange(testUserId, TransactionType.INCOME, any(), any()))
            .thenReturn(25000.0)
        whenever(transactionRepository.getSumByUserTypeAndDateRange(testUserId, TransactionType.EXPENSE, any(), any()))
            .thenReturn(500.0)

        val viewModel = HomeViewModel(userRepository, transactionRepository, categoryRepository, syncRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(25000.0, viewModel.uiState.value.monthlyIncome, 0.01)
        assertEquals(500.0, viewModel.uiState.value.monthlyExpense, 0.01)
        assertEquals(24500.0, viewModel.uiState.value.monthlyBalance, 0.01)
    }

    @Test
    fun `empty transactions list shows empty state`() = runTest {
        whenever(userRepository.getCurrentUserId()).thenReturn(testUserId)
        whenever(categoryRepository.getCategoriesByUserOnce(testUserId))
            .thenReturn(listOf(testCategory))
        whenever(transactionRepository.getTransactionsByUserAndDateRange(any(), any(), any()))
            .thenReturn(flowOf(emptyList()))
        whenever(transactionRepository.getSumByUserTypeAndDateRange(any(), any(), any(), any()))
            .thenReturn(0.0)

        val viewModel = HomeViewModel(userRepository, transactionRepository, categoryRepository, syncRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.transactions.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
