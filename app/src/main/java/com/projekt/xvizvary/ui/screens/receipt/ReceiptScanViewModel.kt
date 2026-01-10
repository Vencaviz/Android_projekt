package com.projekt.xvizvary.ui.screens.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.database.model.Receipt
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.repository.ReceiptRepository
import com.projekt.xvizvary.database.repository.TransactionRepository
import com.projekt.xvizvary.mlkit.ParsedReceipt
import com.projekt.xvizvary.mlkit.ReceiptParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ScanState {
    CAMERA,      // Showing camera preview
    PREVIEW,     // Showing parsed data for confirmation
    SAVING       // Saving data
}

data class ReceiptScanUiState(
    val scanState: ScanState = ScanState.CAMERA,
    val recognizedText: String = "",
    val parsedReceipt: ParsedReceipt? = null,
    val editableStoreName: String = "",
    val editableAmount: String = "",
    val editableDate: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class ReceiptScanEvent {
    data object ReceiptSaved : ReceiptScanEvent()
    data class Error(val message: String) : ReceiptScanEvent()
}

@HiltViewModel
class ReceiptScanViewModel @Inject constructor(
    private val receiptParser: ReceiptParser,
    private val receiptRepository: ReceiptRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceiptScanUiState())
    val uiState: StateFlow<ReceiptScanUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ReceiptScanEvent>()
    val events: SharedFlow<ReceiptScanEvent> = _events.asSharedFlow()

    /**
     * Called when text is recognized from camera
     */
    fun onTextRecognized(text: String) {
        if (_uiState.value.scanState != ScanState.CAMERA) return
        
        _uiState.value = _uiState.value.copy(recognizedText = text)
    }

    /**
     * Process the recognized text and show preview
     */
    fun captureAndParse() {
        val text = _uiState.value.recognizedText
        if (text.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No text recognized. Please try again."
            )
            return
        }

        val parsed = receiptParser.parse(text)
        
        _uiState.value = _uiState.value.copy(
            scanState = ScanState.PREVIEW,
            parsedReceipt = parsed,
            editableStoreName = parsed.storeName ?: "",
            editableAmount = parsed.totalAmount?.toString() ?: "",
            editableDate = parsed.date ?: "",
            errorMessage = null
        )
    }

    /**
     * Go back to camera view
     */
    fun backToCamera() {
        _uiState.value = ReceiptScanUiState()
    }

    /**
     * Update editable fields
     */
    fun onStoreNameChange(name: String) {
        _uiState.value = _uiState.value.copy(editableStoreName = name)
    }

    fun onAmountChange(amount: String) {
        val filtered = amount.filter { it.isDigit() || it == '.' || it == ',' }
            .replace(',', '.')
        _uiState.value = _uiState.value.copy(editableAmount = filtered)
    }

    fun onDateChange(date: String) {
        _uiState.value = _uiState.value.copy(editableDate = date)
    }

    /**
     * Save the receipt and create a transaction
     */
    fun saveReceipt() {
        val state = _uiState.value
        
        val amount = state.editableAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Invalid amount")
            return
        }

        val storeName = state.editableStoreName.ifBlank { "Unknown Store" }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                scanState = ScanState.SAVING,
                isLoading = true
            )

            try {
                // Create transaction
                val transaction = Transaction(
                    name = storeName,
                    amount = amount,
                    type = TransactionType.EXPENSE,
                    categoryId = null, // Could be categorized later
                    date = System.currentTimeMillis(),
                    note = "Scanned from receipt"
                )
                
                val transactionId = transactionRepository.insertTransaction(transaction)

                // Save receipt with link to transaction
                val receipt = Receipt(
                    storeName = storeName,
                    totalAmount = amount,
                    date = System.currentTimeMillis(),
                    rawText = state.parsedReceipt?.rawText ?: "",
                    transactionId = transactionId
                )
                
                receiptRepository.insertReceipt(receipt)

                _events.emit(ReceiptScanEvent.ReceiptSaved)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    scanState = ScanState.PREVIEW,
                    isLoading = false,
                    errorMessage = e.message
                )
                _events.emit(ReceiptScanEvent.Error(e.message ?: "Failed to save receipt"))
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
