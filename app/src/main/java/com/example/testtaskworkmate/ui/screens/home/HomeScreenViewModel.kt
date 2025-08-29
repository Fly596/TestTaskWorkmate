package com.example.testtaskworkmate.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtaskworkmate.data.repos.RamRepository
import com.example.testtaskworkmate.data.source.local.CharacterFilters
import com.example.testtaskworkmate.data.source.network.NetworkCharacter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class HomeScreenUiState(
    val characters: List<NetworkCharacter> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val characterFilters: CharacterFilters? = null,
    val name: String? = null,
    val status: String? = null,
    val gender: String? = null,
    val species: String? = null,
    val type: String? = null,
)

@HiltViewModel
class HomeScreenViewModel
@Inject
constructor(private val ramRepo: RamRepository) : ViewModel() {

    // Приватные StateFlow для каждого фильтра
    private val _statusFilter = MutableStateFlow<String?>(null)
    private val _genderFilter = MutableStateFlow<String?>(null)

    val uiStaten: StateFlow<HomeScreenUiState> = combine(
        ramRepo.getCharactersFlow(),
        _statusFilter, _genderFilter
    ) { characters, status, gender ->
        val filteredList = characters.filter { character ->
            (status == null || character.status.equals(
                status,
                ignoreCase = true
            )) &&
                    (gender == null || character.gender.equals(
                        gender,
                        ignoreCase = true
                    ))
        }

        HomeScreenUiState(characters = filteredList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenUiState(isLoading = true)
    )

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            refreshData()
            /*        _uiState.update { it.copy(isLoading = true) }

                   ramRepo.refresh()
                   ramRepo.getCharactersFlow().collect { characters ->
                       _uiState.update {
                           it.copy(
                               characters = characters,
                               isLoading = false,
                           )
                       }
                   } */
        }
        // Загрузка данных при инициализации ViewModel.
        // getCharacters()
    }

    fun refreshData() {
        viewModelScope.launch {
            // Мы не ждем окончания refresh. Flow сам обновит UI, когда данные придут.
            ramRepo.refresh()
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
    }

    // Фильтрация персонажей.
    fun filterCharacters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            if (_uiState.value.status == "not selected") {
                _uiState.update { it.copy(status = null) }
            }
            if (_uiState.value.species == "not selected") {
                _uiState.update { it.copy(species = null) }
            }

            if (_uiState.value.type == "not selected") {
                _uiState.update { it.copy(type = null) }
            }
            if (_uiState.value.gender == "not selected") {
                _uiState.update { it.copy(gender = null) }
            }
            withContext(Dispatchers.IO) {
                val filters: CharacterFilters =
                    CharacterFilters(
                        name = _uiState.value.name,
                        status = _uiState.value.status,
                        genders = _uiState.value.gender,
                        species = _uiState.value.species,
                        types = _uiState.value.type,
                    )

                val filteredData =
                    ramRepo.getFilteredCharacters(filters)
                        .map { filteredCharacters ->
                            if (filteredCharacters.isNotEmpty()) {
                                _uiState.update {
                                    it.copy(characters = filteredCharacters)
                                }
                            } else {
                                _uiState.update {
                                    it.copy(
                                        error = "No characters found",
                                        characters = emptyList(),
                                    )
                                }
                            }
                        }
                /* if (filteredData.isNotEmpty()) {
                    _uiState.update { it.copy(characters = filteredData) }
                } else {
                    _uiState.update {
                        it.copy(
                            error = "No characters found",
                            characters = emptyList(),
                        )
                    }
                } */
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
    fun resetFilters() {
        _statusFilter.value = null
        _genderFilter.value = null
    }
    /*fun resetFilters() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    characterFilters = null,
                    name = null,
                    status = null,
                    gender = null,
                    species = null,
                    type = null,
                )
            }
            val characterFilters = CharacterFilters()
            val filteredCharacters =
                ramRepo.getFilteredCharacters(characterFilters).map {
                    filterCharacters ->
                    _uiState.update { it.copy(characters = filterCharacters) }
                }
             _uiState.update {
                it.copy(
                    characters = filteredCharacters,
                )
            }

            ramRepo.refresh()
        }
    }*/

    fun updateCharactersFilters(characterFilters: CharacterFilters?) {
        _uiState.update { it.copy(characterFilters = characterFilters) }
    }

    fun nameFilterChanged(name: String?) {
        _uiState.update { it.copy(name = name) }
    }

    fun statusFilterChanged(status: String?) {
        _statusFilter.value = if (status == "not selected") null else status
    }

    fun genderFilterChanged(gender: String?) {
        _genderFilter.value = if (gender == "not selected") null else gender
    }


    fun speciesFilterChanged(species: String?) {
        _uiState.update { it.copy(species = species) }
    }


    fun typeFilterChanged(type: String?) {
        _uiState.update { it.copy(type = type) }
    }
}
