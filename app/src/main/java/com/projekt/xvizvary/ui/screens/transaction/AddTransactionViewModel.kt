package com.projekt.xvizvary.ui.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.auth.repository.UserRepository
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.repository.CategoryRepository
import com.projekt.xvizvary.sync.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TransactionTypeSelection {
    INCOME,
    EXPENSE
}

data class AddTransactionUiState(
    val name: String = "",
    val amount: String = "",
    val type: TransactionTypeSelection = TransactionTypeSelection.EXPENSE,
    val selectedCategoryId: String? = null,
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val amountError: String? = null
)

sealed class AddTransactionEvent {
    data object TransactionSaved : AddTransactionEvent()
    data class Error(val message: String) : AddTransactionEvent()
}

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddTransactionEvent>()
    val events: SharedFlow<AddTransactionEvent> = _events.asSharedFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        val userId = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            // Load categories from local DB
            categoryRepository.getCategoriesByUser(userId).collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, nameError = null)
    }

    fun onAmountChange(amount: String) {
        // Only allow valid number input
        val filtered = amount.filter { it.isDigit() || it == '.' || it == ',' }
            .replace(',', '.')
        _uiState.value = _uiState.value.copy(amount = filtered, amountError = null)
    }

    fun onTypeChange(type: TransactionTypeSelection) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun onCategoryChange(categoryId: String?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun onDateChange(date: Long) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun onNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun saveTransaction() {
        val userId = userRepository.getCurrentUserId()
        if (userId == null) {
            viewModelScope.launch {
                _events.emit(AddTransactionEvent.Error("User not logged in"))
            }
            return
        }

        val state = _uiState.value

        // Validation
        var hasError = false

        if (state.name.isBlank()) {
            _uiState.value = _uiState.value.copy(nameError = "error_empty_name")
            hasError = true
        }

        val amountValue = state.amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            _uiState.value = _uiState.value.copy(amountError = "error_invalid_amount")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val transaction = Transaction(
                    userId = userId,
                    name = state.name.trim(),
                    amount = amountValue!!,
                    type = TransactionType.valueOf(state.type.name),
                    categoryId = state.selectedCategoryId,
                    date = state.date,
                    note = state.note.takeIf { it.isNotBlank() }
                )

                // Save via SyncRepository (saves to both local and cloud)
                syncRepository.addTransaction(userId, transaction)
                _events.emit(AddTransactionEvent.TransactionSaved)
            } catch (e: Exception) {
                _events.emit(AddTransactionEvent.Error(e.message ?: "Unknown error"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
