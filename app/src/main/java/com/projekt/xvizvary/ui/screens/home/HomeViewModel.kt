package com.projekt.xvizvary.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.auth.repository.UserRepository
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.repository.CategoryRepository
import com.projekt.xvizvary.database.repository.TransactionRepository
import com.projekt.xvizvary.sync.SyncRepository
import com.projekt.xvizvary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionWithCategoryDisplay(
    val transaction: Transaction,
    val category: Category?
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val transactions: List<TransactionWithCategoryDisplay> = emptyList(),
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val monthlyBalance: Double = 0.0,
    val currentMonth: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var categories: Map<String, Category> = emptyMap()

    init {
        loadData()
    }

    private fun loadData() {
        val userId = userRepository.getCurrentUserId() ?: return
        val startOfMonth = DateUtils.getStartOfMonth()
        val endOfMonth = DateUtils.getEndOfMonth()

        viewModelScope.launch {
            // Load categories from local DB
            categories = categoryRepository.getCategoriesByUserOnce(userId)
                .associateBy { it.firestoreId }

            // Load transactions for current month from local DB
            transactionRepository.getTransactionsByUserAndDateRange(userId, startOfMonth, endOfMonth)
                .collect { transactions ->
                    // Map transactions with their categories
                    val transactionsWithCategory = transactions.map { tx ->
                        TransactionWithCategoryDisplay(
                            transaction = tx,
                            category = tx.categoryId?.let { categories[it] }
                        )
                    }

                    // Calculate monthly totals from local DB
                    val income = transactionRepository.getSumByUserTypeAndDateRange(
                        userId, TransactionType.INCOME, startOfMonth, endOfMonth
                    )
                    val expense = transactionRepository.getSumByUserTypeAndDateRange(
                        userId, TransactionType.EXPENSE, startOfMonth, endOfMonth
                    )

                    _uiState.value = HomeUiState(
                        isLoading = false,
                        transactions = transactionsWithCategory,
                        monthlyIncome = income,
                        monthlyExpense = expense,
                        monthlyBalance = income - expense,
                        currentMonth = DateUtils.getCurrentMonthName()
                    )
                }
        }
    }

    fun deleteTransaction(transaction: TransactionWithCategoryDisplay) {
        val userId = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            syncRepository.deleteTransaction(userId, transaction.transaction.firestoreId)
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadData()
    }
}
