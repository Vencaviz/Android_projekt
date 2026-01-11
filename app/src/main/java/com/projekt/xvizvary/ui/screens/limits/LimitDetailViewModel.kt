package com.projekt.xvizvary.ui.screens.limits

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.auth.repository.UserRepository
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Limit
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.repository.CategoryRepository
import com.projekt.xvizvary.database.repository.LimitRepository
import com.projekt.xvizvary.database.repository.TransactionRepository
import com.projekt.xvizvary.sync.SyncRepository
import com.projekt.xvizvary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LimitDetailUiState(
    val isLoading: Boolean = true,
    val isAddMode: Boolean = false,
    // Detail mode
    val limit: Limit? = null,
    val category: Category? = null,
    val transactions: List<Transaction> = emptyList(),
    val spentAmount: Double = 0.0,
    val dailySpending: List<DailySpending> = emptyList(),
    // Add mode
    val selectedCategoryId: String? = null,
    val limitAmount: String = "",
    val availableCategories: List<Category> = emptyList(),
    val categoryError: String? = null,
    val amountError: String? = null
)

data class DailySpending(
    val dayOfMonth: Int,
    val amount: Double,
    val label: String
)

sealed class LimitDetailEvent {
    data object LimitSaved : LimitDetailEvent()
    data class Error(val message: String) : LimitDetailEvent()
}

@HiltViewModel
class LimitDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val limitRepository: LimitRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LimitDetailUiState())
    val uiState: StateFlow<LimitDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LimitDetailEvent>()
    val events: SharedFlow<LimitDetailEvent> = _events.asSharedFlow()

    private val limitIdParam: String = savedStateHandle.get<String>("limitId") ?: "0"
    private val isAddMode = limitIdParam == "0"

    init {
        if (isAddMode) {
            loadAddMode()
        } else {
            loadDetailMode(limitIdParam)
        }
    }

    private fun loadAddMode() {
        val userId = userRepository.getCurrentUserId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isAddMode = true)

            val allCategories = categoryRepository.getCategoriesByUserOnce(userId)
            val limits = limitRepository.getLimitsByUser(userId).first()
            val categoriesWithLimits = limits.map { it.categoryId }.toSet()
            val availableCategories = allCategories.filter { it.firestoreId !in categoriesWithLimits }

            _uiState.value = LimitDetailUiState(
                isLoading = false,
                isAddMode = true,
                availableCategories = availableCategories
            )
        }
    }

    private fun loadDetailMode(limitId: String) {
        val userId = userRepository.getCurrentUserId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isAddMode = false)

            val limit = limitRepository.getLimitByFirestoreId(limitId)
            if (limit == null) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                return@launch
            }

            val category = categoryRepository.getCategoryByFirestoreId(limit.categoryId)
            
            val startOfMonth = DateUtils.getStartOfMonth()
            val endOfMonth = DateUtils.getEndOfMonth()

            // Get transactions for this category
            val allTransactions = transactionRepository.getTransactionsByUserOnce(userId)
            val categoryTransactions = allTransactions
                .filter { 
                    it.categoryId == limit.categoryId && 
                    it.type == TransactionType.EXPENSE &&
                    it.date in startOfMonth..endOfMonth
                }
                .sortedByDescending { it.date }

            val spentAmount = categoryTransactions.sumOf { it.amount }

            // Calculate daily spending for chart
            val dailySpending = calculateDailySpending(categoryTransactions, startOfMonth, endOfMonth)

            _uiState.value = LimitDetailUiState(
                isLoading = false,
                isAddMode = false,
                limit = limit,
                category = category,
                transactions = categoryTransactions,
                spentAmount = spentAmount,
                dailySpending = dailySpending
            )
        }
    }

    private fun calculateDailySpending(
        transactions: List<Transaction>,
        startOfMonth: Long,
        endOfMonth: Long
    ): List<DailySpending> {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = startOfMonth
        val daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)

        val dailyMap = mutableMapOf<Int, Double>()
        
        transactions.forEach { tx ->
            calendar.timeInMillis = tx.date
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            dailyMap[day] = (dailyMap[day] ?: 0.0) + tx.amount
        }

        return (1..daysInMonth).map { day ->
            DailySpending(
                dayOfMonth = day,
                amount = dailyMap[day] ?: 0.0,
                label = day.toString()
            )
        }
    }

    fun onCategoryChange(categoryId: String?) {
        _uiState.value = _uiState.value.copy(
            selectedCategoryId = categoryId,
            categoryError = null
        )
    }

    fun onAmountChange(amount: String) {
        val filtered = amount.filter { it.isDigit() || it == '.' || it == ',' }
            .replace(',', '.')
        _uiState.value = _uiState.value.copy(limitAmount = filtered, amountError = null)
    }

    fun saveLimit() {
        val userId = userRepository.getCurrentUserId()
        if (userId == null) {
            viewModelScope.launch {
                _events.emit(LimitDetailEvent.Error("User not logged in"))
            }
            return
        }

        val state = _uiState.value

        var hasError = false

        if (state.selectedCategoryId == null) {
            _uiState.value = _uiState.value.copy(categoryError = "error_select_category")
            hasError = true
        }

        val amountValue = state.limitAmount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            _uiState.value = _uiState.value.copy(amountError = "error_invalid_amount")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val limit = Limit(
                    userId = userId,
                    categoryId = state.selectedCategoryId!!,
                    limitAmount = amountValue!!
                )
                syncRepository.addLimit(userId, limit)
                _events.emit(LimitDetailEvent.LimitSaved)
            } catch (e: Exception) {
                _events.emit(LimitDetailEvent.Error(e.message ?: "Unknown error"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun refresh() {
        if (!isAddMode) {
            loadDetailMode(limitIdParam)
        }
    }
}
