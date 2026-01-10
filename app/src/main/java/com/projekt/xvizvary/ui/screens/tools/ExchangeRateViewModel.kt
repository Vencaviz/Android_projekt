package com.projekt.xvizvary.ui.screens.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.communication.CommunicationResult
import com.projekt.xvizvary.communication.IExchangeRateRemoteRepository
import com.projekt.xvizvary.network.model.ExchangeRate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExchangeRateUiState(
    val isLoading: Boolean = true,
    val rates: List<ExchangeRate> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(
    private val exchangeRateRepository: IExchangeRateRemoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeRateUiState())
    val uiState: StateFlow<ExchangeRateUiState> = _uiState.asStateFlow()

    init {
        loadExchangeRates()
    }

    fun loadExchangeRates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = exchangeRateRepository.getLatestRates()) {
                is CommunicationResult.Success -> {
                    _uiState.value = ExchangeRateUiState(
                        isLoading = false,
                        rates = result.data,
                        error = null
                    )
                }
                is CommunicationResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.error.message ?: "Error ${result.error.code}"
                    )
                }
                is CommunicationResult.ConnectionError -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Connection error. Please check your internet connection."
                    )
                }
                is CommunicationResult.Exception -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadExchangeRates()
    }
}
