package com.projekt.xvizvary.ui.screens.limits

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Limit
import com.projekt.xvizvary.database.repository.CategoryRepository
import com.projekt.xvizvary.database.repository.LimitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LimitDetailUiState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val limitId: Long? = null,
    val selectedCategoryId: Long? = null,
    val limitAmount: String = "",
    val categories: List<Category> = emptyList(),
    val availableCategories: List<Category> = emptyList(), // Categories without limits
    val categoryError: String? = null,
    val amountError: String? = null
)

sealed class LimitDetailEvent {
    data object LimitSaved : LimitDetailEvent()
    data class Error(val message: String) : LimitDetailEvent()
}

@HiltViewModel
class LimitDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val limitRepository: LimitRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LimitDetailUiState())
    val uiState: StateFlow<LimitDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LimitDetailEvent>()
    val events: SharedFlow<LimitDetailEvent> = _events.asSharedFlow()

    private val limitId: Long? = savedStateHandle.get<Long>("limitId")?.takeIf { it > 0 }

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val allCategories = categoryRepository.getAllCategoriesOnce()

            if (limitId != null) {
                // Edit mode - load existing limit
                val limitWithCategory = limitRepository.getLimitByIdWithCategory(limitId)
                if (limitWithCategory != null) {
                    _uiState.value = LimitDetailUiState(
                        isLoading = false,
                        isEditMode = true,
                        limitId = limitId,
                        selectedCategoryId = limitWithCategory.limit.categoryId,
                        limitAmount = limitWithCategory.limit.limitAmount.toString(),
                        categories = allCategories,
                        availableCategories = allCategories // In edit mode, current category is always available
                    )
                }
            } else {
                // Add mode - find categories without limits
                val categoriesWithLimits = mutableSetOf<Long>()
                limitRepository.getAllLimits().collect { limits ->
                    limits.forEach { limit ->
                        categoriesWithLimits.add(limit.categoryId)
                    }
                    
                    val availableCategories = allCategories.filter { it.id !in categoriesWithLimits }
                    
                    _uiState.value = LimitDetailUiState(
                        isLoading = false,
                        isEditMode = false,
                        categories = allCategories,
                        availableCategories = availableCategories
                    )
                }
            }
        }
    }

    fun onCategoryChange(categoryId: Long?) {
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
        val state = _uiState.value

        // Validation
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
                if (state.isEditMode && state.limitId != null) {
                    // Update existing limit
                    val existingLimit = limitRepository.getLimitById(state.limitId)
                    if (existingLimit != null) {
                        limitRepository.updateLimit(
                            existingLimit.copy(
                                categoryId = state.selectedCategoryId!!,
                                limitAmount = amountValue!!
                            )
                        )
                    }
                } else {
                    // Create new limit
                    val limit = Limit(
                        categoryId = state.selectedCategoryId!!,
                        limitAmount = amountValue!!
                    )
                    limitRepository.insertLimit(limit)
                }

                _events.emit(LimitDetailEvent.LimitSaved)
            } catch (e: Exception) {
                _events.emit(LimitDetailEvent.Error(e.message ?: "Unknown error"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
