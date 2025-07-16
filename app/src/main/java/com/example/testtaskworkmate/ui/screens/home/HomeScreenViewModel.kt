package com.example.testtaskworkmate.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class HomeScreenUiState(
    val characters: List<NetworkCharacter> = emptyList(),
    val searchQuery: String = "",
    val nameFilter: String = "",
    val statusFilter: String? = null,
    val speciesFilter: String? = null,
    val genderFilter: String? = null,
    val typeFilter: String? = null,
)

@HiltViewModel
class HomeScreenViewModel
@Inject
constructor(private val ramRepo: RamRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Загрузка данных при инициализации ViewModel.
        getCharacters()
    }

    private fun getCharacters() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val characters = ramRepo.fetchCharacters()
                _uiState.update { it.copy(characters = characters) }
            }
        }
    }

    fun onSearchByNameQuerySubmitted(query: String) {
        viewModelScope.launch {
            val filteredCharacters =
                if (query.isEmpty()) {
                    _uiState.value.characters
                } else {
                    ramRepo.getCharactersByName(query)
                }
            _uiState.update { it.copy(characters = filteredCharacters) }
        }

        // TODO: настроить поля фильтра.
        /*         viewModelScope.launch {
        val filteredCharacters =
            if (query.isEmpty()) {
                _uiState.value.characters
            } else {
                ramRepo.getFilteredNetworkCharacters(
                    name = query,
                    status = _uiState.value.statusFilter,
                    species = _uiState.value.speciesFilter,
                    gender = _uiState.value.genderFilter,
                    type = _uiState.value.typeFilter
                )
                 */
        /* _uiState.value.characters.filter {
            it.name.contains(query, ignoreCase = true)
        } */
        /*
                }
            _uiState.update { it.copy(characters = filteredCharacters) }
        } */

    }

    fun nameFilterChanged(name: String) {
        _uiState.update { it.copy(nameFilter = name) }
    }

    fun statusFilterChanged(status: String) {
        _uiState.update { it.copy(statusFilter = status) }
    }

    fun speciesFilterChanged(species: String) {
        _uiState.update { it.copy(speciesFilter = species) }
    }

    fun genderFilterChanged(gender: String) {
        _uiState.update { it.copy(genderFilter = gender) }
    }

    fun typeFilterChanged(type: String) {
        _uiState.update { it.copy(typeFilter = type) }
    }
}
