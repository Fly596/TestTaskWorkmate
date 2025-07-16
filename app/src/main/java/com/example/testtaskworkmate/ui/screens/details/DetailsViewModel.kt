package com.example.testtaskworkmate.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.testtaskworkmate.Details
import com.example.testtaskworkmate.data.repos.RamRepository
import com.example.testtaskworkmate.data.source.network.NetworkCharacter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class DetailsUiState(val character: NetworkCharacter? = null)

@HiltViewModel
class DetailsViewModel
@Inject
constructor(
    private val ramRepo: RamRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // val characterId: Int = checkNotNull(savedStateHandle["characterId"])

    // 1. Получить данные из бд.
    // 2. Обновить state.

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val args = savedStateHandle.toRoute<Details>()
                    val characterId = args.id
                    getCharacter(characterId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        // getCharacter(characterId)
    }

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState = _uiState.asStateFlow()

    private fun getCharacter(id: Int) {
        viewModelScope.launch {
            try {
                val character = ramRepo.getCharacter(id)
                _uiState.update { it.copy(character = character) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
