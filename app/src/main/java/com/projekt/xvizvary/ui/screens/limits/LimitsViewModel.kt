package com.projekt.xvizvary.ui.screens.limits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.database.model.LimitWithSpent
import com.projekt.xvizvary.database.repository.LimitRepository
import com.projekt.xvizvary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LimitsUiState(
    val isLoading: Boolean = true,
    val limits: List<LimitWithSpent> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class LimitsViewModel @Inject constructor(
    private val limitRepository: LimitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LimitsUiState())
    val uiState: StateFlow<LimitsUiState> = _uiState.asStateFlow()

    init {
        loadLimits()
    }

    fun loadLimits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val startOfMonth = DateUtils.getStartOfMonth()
                val endOfMonth = DateUtils.getEndOfMonth()

                val limitsWithSpent = limitRepository.getLimitsWithSpent(startOfMonth, endOfMonth)

                _uiState.value = LimitsUiState(
                    isLoading = false,
                    limits = limitsWithSpent
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteLimit(limitWithSpent: LimitWithSpent) {
        viewModelScope.launch {
            limitRepository.deleteLimit(limitWithSpent.limit)
            loadLimits()
        }
    }

    fun refresh() {
        loadLimits()
    }
}
