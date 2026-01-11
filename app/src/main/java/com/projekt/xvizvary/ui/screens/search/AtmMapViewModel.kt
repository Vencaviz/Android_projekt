package com.projekt.xvizvary.ui.screens.search

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.projekt.xvizvary.data.model.Atm
import com.projekt.xvizvary.data.model.AtmSampleData
import com.projekt.xvizvary.data.model.BankType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AtmMapUiState(
    val atms: List<Atm> = emptyList(),
    val filteredAtms: List<Atm> = emptyList(),
    val searchQuery: String = "",
    val selectedBank: BankType = BankType.ALL,
    val selectedAtm: Atm? = null,
    val cameraPosition: LatLng = LatLng(49.1951, 16.6068), // Brno center
    val zoomLevel: Float = 13f
)

@HiltViewModel
class AtmMapViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AtmMapUiState())
    val uiState: StateFlow<AtmMapUiState> = _uiState.asStateFlow()

    init {
        loadAtms()
    }

    private fun loadAtms() {
        val allAtms = AtmSampleData.atms
        _uiState.value = _uiState.value.copy(
            atms = allAtms,
            filteredAtms = allAtms
        )
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterAtms()
    }

    fun onBankFilterChange(bank: BankType) {
        _uiState.value = _uiState.value.copy(selectedBank = bank)
        filterAtms()
    }

    private fun filterAtms() {
        val state = _uiState.value
        var filtered = state.atms

        // Filter by bank
        if (state.selectedBank != BankType.ALL) {
            filtered = filtered.filter { 
                it.bankName.equals(state.selectedBank.displayName, ignoreCase = true) 
            }
        }

        // Filter by search query
        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase()
            filtered = filtered.filter {
                it.bankName.lowercase().contains(query) ||
                it.address.lowercase().contains(query)
            }
        }

        _uiState.value = _uiState.value.copy(filteredAtms = filtered)
    }

    fun onAtmSelected(atm: Atm?) {
        _uiState.value = _uiState.value.copy(
            selectedAtm = atm,
            cameraPosition = atm?.position ?: _uiState.value.cameraPosition,
            zoomLevel = if (atm != null) 16f else 13f
        )
    }

    fun onAtmListItemClick(atm: Atm) {
        onAtmSelected(atm)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedAtm = null)
    }
}
