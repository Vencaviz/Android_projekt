package com.example.homework2.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework2.R
import com.example.homework2.communication.CommunicationResult
import com.example.homework2.communication.IAPIRemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor(  private val repository: IAPIRemoteRepository) : ViewModel(),
    MapScreenActions{
    private val _uiState: MutableStateFlow<MapScreenUIState> = MutableStateFlow(value = MapScreenUIState())
    val uiState: StateFlow<MapScreenUIState> get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                repository.getRates()
            }
            when(result){
                is CommunicationResult.ConnectionError -> {
                    _uiState.value = _uiState.value.copy(
                        error = MapScreenError(R.string.no_internet_connection)
                    )
                }
                is CommunicationResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = MapScreenError(R.string.failed_to_load_data)
                    )
                }
                is CommunicationResult.Exception -> {
                    _uiState.value = _uiState.value.copy(
                        error = MapScreenError(R.string.exception)
                    )
                }
                is CommunicationResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            data = MapScreenData(  result.data)

                            )
                _uiState.value.Loading = false
                }
            }


        }
    }

}

