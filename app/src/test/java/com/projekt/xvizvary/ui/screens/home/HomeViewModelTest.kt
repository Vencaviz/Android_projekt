package com.projekt.xvizvary.ui.screens.home

import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.model.TransactionWithCategory
import com.projekt.xvizvary.database.repository.CategoryRepository
import com.projekt.xvizvary.database.repository.TransactionRepository
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
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var categoryRepository: CategoryRepository

    private val testCategory = Category(
        id = 1,
        name = "Food",
        icon = "restaurant",
        color = 0xFFE57373
    )

    private val testTransactions = listOf(
        TransactionWithCategory(
            transaction = Transaction(
                id = 1,
                name = "Grocery",
                amount = 500.0,
                type = TransactionType.EXPENSE,
                categoryId = 1,
                date = System.currentTimeMillis()
            ),
            category = testCategory
        ),
        TransactionWithCategory(
            transaction = Transaction(
                id = 2,
                name = "Salary",
                amount = 25000.0,
                type = TransactionType.INCOME,
                categoryId = null,
                date = System.currentTimeMillis()
            ),
            category = null
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        transactionRepository = mock()
        categoryRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        whenever(transactionRepository.getTransactionsByDateRangeWithCategory(any(), any()))
            .thenReturn(flowOf(emptyList()))
        whenever(transactionRepository.getSumByTypeAndDateRange(any(), any(), any()))
            .thenReturn(0.0)
        whenever(categoryRepository.initializeDefaultCategories()).thenReturn(Unit)

        val viewModel = HomeViewModel(transactionRepository, categoryRepository)

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `transactions are loaded correctly`() = runTest {
        whenever(transactionRepository.getTransactionsByDateRangeWithCategory(any(), any()))
            .thenReturn(flowOf(testTransactions))
        whenever(transactionRepository.getSumByTypeAndDateRange(any(), any(), any()))
            .thenReturn(0.0)
        whenever(categoryRepository.initializeDefaultCategories()).thenReturn(Unit)

        val viewModel = HomeViewModel(transactionRepository, categoryRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.transactions.size)
    }

    @Test
    fun `monthly balance is calculated correctly`() = runTest {
        whenever(transactionRepository.getTransactionsByDateRangeWithCategory(any(), any()))
            .thenReturn(flowOf(testTransactions))
        whenever(transactionRepository.getSumByTypeAndDateRange(TransactionType.INCOME, any(), any()))
            .thenReturn(25000.0)
        whenever(transactionRepository.getSumByTypeAndDateRange(TransactionType.EXPENSE, any(), any()))
            .thenReturn(500.0)
        whenever(categoryRepository.initializeDefaultCategories()).thenReturn(Unit)

        val viewModel = HomeViewModel(transactionRepository, categoryRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(25000.0, viewModel.uiState.value.monthlyIncome, 0.01)
        assertEquals(500.0, viewModel.uiState.value.monthlyExpense, 0.01)
        assertEquals(24500.0, viewModel.uiState.value.monthlyBalance, 0.01)
    }

    @Test
    fun `empty transactions list shows empty state`() = runTest {
        whenever(transactionRepository.getTransactionsByDateRangeWithCategory(any(), any()))
            .thenReturn(flowOf(emptyList()))
        whenever(transactionRepository.getSumByTypeAndDateRange(any(), any(), any()))
            .thenReturn(0.0)
        whenever(categoryRepository.initializeDefaultCategories()).thenReturn(Unit)

        val viewModel = HomeViewModel(transactionRepository, categoryRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.transactions.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
