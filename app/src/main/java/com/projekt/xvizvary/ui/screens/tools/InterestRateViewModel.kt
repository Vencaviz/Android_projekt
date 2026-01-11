package com.projekt.xvizvary.ui.screens.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.communication.CommunicationResult
import com.projekt.xvizvary.communication.IInterestRateRemoteRepository
import com.projekt.xvizvary.network.model.HistoricalRate
import com.projekt.xvizvary.network.model.InterestRateDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InterestRateUiState(
    val isLoading: Boolean = true,
    val rates: List<InterestRateDisplay> = emptyList(),
    val selectedRate: InterestRateDisplay? = null,
    val historicalData: List<HistoricalRate> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class InterestRateViewModel @Inject constructor(
    private val interestRateRepository: IInterestRateRemoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InterestRateUiState())
    val uiState: StateFlow<InterestRateUiState> = _uiState.asStateFlow()

    init {
        loadInterestRates()
    }

    fun loadInterestRates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = interestRateRepository.getAllInterestRates()) {
                is CommunicationResult.Success -> {
                    _uiState.value = InterestRateUiState(
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

    fun selectRate(rate: InterestRateDisplay) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedRate = rate, isLoading = true)
            
            when (val result = interestRateRepository.getHistoricalData(rate.id)) {
                is CommunicationResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        historicalData = result.data,
                        isLoading = false
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(
                        historicalData = emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedRate = null, historicalData = emptyList())
    }

    fun refresh() {
        loadInterestRates()
    }
}
