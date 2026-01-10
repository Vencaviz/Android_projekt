package com.projekt.xvizvary.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.model.TransactionWithCategory
import com.projekt.xvizvary.database.repository.CategoryRepository
import com.projekt.xvizvary.database.repository.TransactionRepository
import com.projekt.xvizvary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val transactions: List<TransactionWithCategory> = emptyList(),
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val monthlyBalance: Double = 0.0,
    val currentMonth: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        initializeCategories()
        loadData()
    }

    private fun initializeCategories() {
        viewModelScope.launch {
            categoryRepository.initializeDefaultCategories()
        }
    }

    private fun loadData() {
        val startOfMonth = DateUtils.getStartOfMonth()
        val endOfMonth = DateUtils.getEndOfMonth()

        viewModelScope.launch {
            // Load transactions for current month
            transactionRepository.getTransactionsByDateRangeWithCategory(startOfMonth, endOfMonth)
                .collect { transactions ->
                    // Calculate monthly totals
                    val income = transactionRepository.getSumByTypeAndDateRange(
                        TransactionType.INCOME, startOfMonth, endOfMonth
                    )
                    val expense = transactionRepository.getSumByTypeAndDateRange(
                        TransactionType.EXPENSE, startOfMonth, endOfMonth
                    )

                    _uiState.value = HomeUiState(
                        isLoading = false,
                        transactions = transactions,
                        monthlyIncome = income,
                        monthlyExpense = expense,
                        monthlyBalance = income - expense,
                        currentMonth = DateUtils.getCurrentMonthName()
                    )
                }
        }
    }

    fun deleteTransaction(transaction: TransactionWithCategory) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction.transaction)
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadData()
    }
}
