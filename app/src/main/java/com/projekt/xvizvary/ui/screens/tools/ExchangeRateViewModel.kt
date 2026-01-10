package com.projekt.xvizvary.ui.screens.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.network.model.ExchangeRate
import com.projekt.xvizvary.network.repository.ExchangeRateRepository
import com.projekt.xvizvary.network.repository.ExchangeRateResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExchangeRateUiState(
    val isLoading: Boolean = true,
    val rates: List<ExchangeRate> = emptyList(),
    val date: String = "",
    val error: String? = null
)

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(
    private val exchangeRateRepository: ExchangeRateRepository
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
                is ExchangeRateResult.Success -> {
                    _uiState.value = ExchangeRateUiState(
                        isLoading = false,
                        rates = result.rates,
                        date = result.date,
                        error = null
                    )
                }
                is ExchangeRateResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is ExchangeRateResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    fun refresh() {
        loadExchangeRates()
    }
}
