package com.projekt.xvizvary.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.auth.repository.UserRepository
import com.projekt.xvizvary.firebase.model.FirestoreCategory
import com.projekt.xvizvary.firebase.model.FirestoreTransaction
import com.projekt.xvizvary.firebase.repository.FirestoreCategoryRepository
import com.projekt.xvizvary.firebase.repository.FirestoreTransactionRepository
import com.projekt.xvizvary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionWithCategoryDisplay(
    val transaction: FirestoreTransaction,
    val category: FirestoreCategory?
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
    private val transactionRepository: FirestoreTransactionRepository,
    private val categoryRepository: FirestoreCategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var categories: Map<String, FirestoreCategory> = emptyMap()

    init {
        loadData()
    }

    private fun loadData() {
        val userId = userRepository.getCurrentUserId() ?: return
        val startOfMonth = DateUtils.getStartOfMonth()
        val endOfMonth = DateUtils.getEndOfMonth()

        viewModelScope.launch {
            // Load categories first
            categories = categoryRepository.getCategoriesOnce(userId)
                .associateBy { it.id }

            // Load transactions for current month
            transactionRepository.getTransactionsByDateRange(userId, startOfMonth, endOfMonth)
                .collect { transactions ->
                    // Map transactions with their categories
                    val transactionsWithCategory = transactions.map { tx ->
                        TransactionWithCategoryDisplay(
                            transaction = tx,
                            category = tx.categoryId?.let { categories[it] }
                        )
                    }

                    // Calculate monthly totals
                    val income = transactionRepository.getSumByTypeAndDateRange(
                        userId, "INCOME", startOfMonth, endOfMonth
                    )
                    val expense = transactionRepository.getSumByTypeAndDateRange(
                        userId, "EXPENSE", startOfMonth, endOfMonth
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
            transactionRepository.deleteTransaction(userId, transaction.transaction.id)
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadData()
    }
}
