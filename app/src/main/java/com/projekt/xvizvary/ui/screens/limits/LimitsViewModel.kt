package com.projekt.xvizvary.ui.screens.limits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.auth.repository.UserRepository
import com.projekt.xvizvary.firebase.model.FirestoreCategory
import com.projekt.xvizvary.firebase.model.FirestoreLimit
import com.projekt.xvizvary.firebase.repository.FirestoreCategoryRepository
import com.projekt.xvizvary.firebase.repository.FirestoreLimitRepository
import com.projekt.xvizvary.firebase.repository.FirestoreTransactionRepository
import com.projekt.xvizvary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LimitWithSpentDisplay(
    val limit: FirestoreLimit,
    val category: FirestoreCategory,
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
    private val limitRepository: FirestoreLimitRepository,
    private val categoryRepository: FirestoreCategoryRepository,
    private val transactionRepository: FirestoreTransactionRepository
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

                // Get all categories
                val categories = categoryRepository.getCategoriesOnce(userId)
                    .associateBy { it.id }

                // Get limits and calculate spent amounts
                limitRepository.getLimits(userId).collect { limits ->
                    val limitsWithSpent = limits.mapNotNull { limit ->
                        val category = categories[limit.categoryId] ?: return@mapNotNull null
                        
                        // Calculate spent amount for this specific category
                        val spent = transactionRepository.getSumByCategoryAndDateRange(
                            userId = userId,
                            categoryId = limit.categoryId,
                            type = "EXPENSE",
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
            limitRepository.deleteLimit(userId, limitWithSpent.limit.id)
        }
    }

    fun refresh() {
        loadLimits()
    }
}
