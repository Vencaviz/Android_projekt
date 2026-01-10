package com.projekt.xvizvary.ui.screens.limits

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.auth.repository.UserRepository
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Limit
import com.projekt.xvizvary.database.repository.CategoryRepository
import com.projekt.xvizvary.database.repository.LimitRepository
import com.projekt.xvizvary.sync.SyncRepository
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
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val limitId: String? = null,
    val selectedCategoryId: String? = null,
    val limitAmount: String = "",
    val categories: List<Category> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
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
    private val userRepository: UserRepository,
    private val limitRepository: LimitRepository,
    private val categoryRepository: CategoryRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LimitDetailUiState())
    val uiState: StateFlow<LimitDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LimitDetailEvent>()
    val events: SharedFlow<LimitDetailEvent> = _events.asSharedFlow()

    // Get limitId from navigation - "0" means new limit
    private val limitIdParam: String = savedStateHandle.get<String>("limitId") ?: "0"
    private val limitId: String? = if (limitIdParam != "0") limitIdParam else null

    init {
        loadData()
    }

    private fun loadData() {
        val userId = userRepository.getCurrentUserId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load categories from local DB
            val allCategories = categoryRepository.getCategoriesByUserOnce(userId)

            if (limitId != null) {
                // Edit mode - load existing limit from local DB
                val limit = limitRepository.getLimitByFirestoreId(limitId)
                if (limit != null) {
                    _uiState.value = LimitDetailUiState(
                        isLoading = false,
                        isEditMode = true,
                        limitId = limitId,
                        selectedCategoryId = limit.categoryId,
                        limitAmount = limit.limitAmount.toString(),
                        categories = allCategories,
                        availableCategories = allCategories
                    )
                }
            } else {
                // Add mode - find categories without limits from local DB
                val limits = limitRepository.getLimitsByUser(userId).first()
                val categoriesWithLimits = limits.map { it.categoryId }.toSet()
                val availableCategories = allCategories.filter { it.firestoreId !in categoriesWithLimits }

                _uiState.value = LimitDetailUiState(
                    isLoading = false,
                    isEditMode = false,
                    categories = allCategories,
                    availableCategories = availableCategories
                )
            }
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
                    val existingLimit = limitRepository.getLimitByFirestoreId(state.limitId)
                    if (existingLimit != null) {
                        val updatedLimit = existingLimit.copy(
                            categoryId = state.selectedCategoryId!!,
                            limitAmount = amountValue!!
                        )
                        syncRepository.updateLimit(userId, updatedLimit)
                    }
                } else {
                    // Create new limit via SyncRepository
                    val limit = Limit(
                        userId = userId,
                        categoryId = state.selectedCategoryId!!,
                        limitAmount = amountValue!!
                    )
                    syncRepository.addLimit(userId, limit)
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
