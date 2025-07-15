package com.example.testtaskworkmate.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtaskworkmate.data.model.Character
import com.example.testtaskworkmate.data.model.RamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeScreenUiState(
    val characters: List<Character> = emptyList(),
    val searchInput: String = "",
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val ramRepository: RamRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(characters = ramRepository.getCharacters()) }
        }
    }

    fun updateSearchInput(input: String) {
        _uiState.update { it.copy(searchInput = input) }
    }
}
