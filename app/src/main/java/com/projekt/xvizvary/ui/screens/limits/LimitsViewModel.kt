package com.projekt.xvizvary.ui.screens.limits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.auth.repository.UserRepository
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Limit
import com.projekt.xvizvary.database.repository.CategoryRepository
import com.projekt.xvizvary.database.repository.LimitRepository
import com.projekt.xvizvary.database.repository.TransactionRepository
import com.projekt.xvizvary.sync.SyncRepository
import com.projekt.xvizvary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LimitWithSpentDisplay(
    val limit: Limit,
    val category: Category,
    val spentAmount: Double
) {
    val remainingAmount: Double
        get() = limit.limitAmount - spentAmount

    val progress: Float
        get() = if (limit.limitAmount > 0) {
            (spentAmount / limit.limitAmount).toFloat().coerceIn(0f, 1f)
        } else 0f

    val isOverBudget: Boolean
        get() = spentAmount > limit.limitAmount
}

data class LimitsUiState(
    val isLoading: Boolean = true,
    val limits: List<LimitWithSpentDisplay> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class LimitsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val limitRepository: LimitRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LimitsUiState())
    val uiState: StateFlow<LimitsUiState> = _uiState.asStateFlow()

    init {
        loadLimits()
    }

    fun loadLimits() {
        val userId = userRepository.getCurrentUserId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val startOfMonth = DateUtils.getStartOfMonth()
                val endOfMonth = DateUtils.getEndOfMonth()

                // Get all categories from local DB
                val categories = categoryRepository.getCategoriesByUserOnce(userId)
                    .associateBy { it.firestoreId }

                // Get limits from local DB and calculate spent amounts
                limitRepository.getLimitsByUser(userId).collect { limits ->
                    val limitsWithSpent = limits.mapNotNull { limit ->
                        val category = categories[limit.categoryId] ?: return@mapNotNull null
                        
                        // Calculate spent amount for this specific category from local DB
                        val spent = transactionRepository.getSpentByUserCategoryAndDateRange(
                            userId = userId,
                            categoryId = limit.categoryId,
                            startDate = startOfMonth,
                            endDate = endOfMonth
                        )

                        LimitWithSpentDisplay(
                            limit = limit,
                            category = category,
                            spentAmount = spent
                        )
                    }

                    _uiState.value = LimitsUiState(
                        isLoading = false,
                        limits = limitsWithSpent
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteLimit(limitWithSpent: LimitWithSpentDisplay) {
        val userId = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            syncRepository.deleteLimit(userId, limitWithSpent.limit.firestoreId)
        }
    }

    fun refresh() {
        loadLimits()
    }
}
