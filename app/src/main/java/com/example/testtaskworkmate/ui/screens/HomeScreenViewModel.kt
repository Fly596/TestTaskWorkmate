package com.example.testtaskworkmate.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtaskworkmate.data.repos.RamRepository
import com.example.testtaskworkmate.data.source.network.NetworkCharacter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeScreenUiState(
    val characters: List<NetworkCharacter> = emptyList(),
    val searchQuery: String = "",
)

@HiltViewModel
class HomeScreenViewModel
@Inject
constructor(
    private val ramRepo: RamRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getCharacters()
    }

    private fun getCharacters() {
        viewModelScope.launch {
            val characters = ramRepo.getNetworkCharacters()
            _uiState.update { it.copy(characters = characters) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSearchQuerySubmitted(query: String) {
        val filteredCharacters =
            if (query.isEmpty()) {
                _uiState.value.characters
            } else {
                _uiState.value.characters.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }
        _uiState.update { it.copy(characters = filteredCharacters) }
    }
}
