package com.example.testtaskworkmate.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtaskworkmate.data.repos.RamRepository
import com.example.testtaskworkmate.data.source.local.CharacterFilters
import com.example.testtaskworkmate.data.source.network.NetworkCharacter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Класс данных, представляющий состояние UI для главного экрана..
data class HomeScreenUiState(
    // Список персонажей для отображения..
    val characters: List<NetworkCharacter> = emptyList(),
    // Флаг состояния загрузки..
    val isLoading: Boolean = false,
    // Сообщение об ошибке..
    val error: String? = null,
    // Объект с текущими фильтрами..
    val characterFilters: CharacterFilters? = null,
    // Значения для каждого фильтра..
    val name: String? = null,
    val status: String? = null,
    val gender: String? = null,
    val species: String? = null,
    val type: String? = null,
)

@HiltViewModel
// ViewModel для главного экрана..
class HomeScreenViewModel
@Inject
constructor(private val ramRepo: RamRepository) : ViewModel() {

    // Приватные StateFlow для хранения состояния каждого фильтра..
    private val _statusFilter = MutableStateFlow<String?>(null)
    private val _genderFilter = MutableStateFlow<String?>(null)
    private val _speciesFilter = MutableStateFlow<String?>(null)

    // Отдельный StateFlow для хранения поискового запроса по имени..
    private val _searchQuery = MutableStateFlow("")

    // Основное состояние UI, созданное реактивным способом с помощью оператора combine..
    val uiStaten: StateFlow<HomeScreenUiState> =
        combine(
            // Объединяем несколько потоков: персонажей из БД и все фильтры..
            ramRepo.getCharactersFlow(),
            _statusFilter,
            _genderFilter,
            _speciesFilter,
            _searchQuery,
        ) { characters, status, gender, species, query ->
            // Этот блок будет выполняться каждый раз, когда изменится любой из потоков выше..
            val filteredList =
                characters.filter { character ->
                    // Проверяем соответствие поисковому запросу по имени..
                    val matchesSearchQuery =
                        if (query.isBlank()) {
                            true // Если запрос пустой, подходят все персонажи..
                        } else {
                            character.name.contains(query, ignoreCase = true)
                        }
                    // Проверяем соответствие фильтру по статусу..
                    val matchesStatus =
                        status == null || character.status.equals(
                            status,
                            ignoreCase = true
                        )
                    // Проверяем соответствие фильтру по полу..
                    val matchesGender =
                        gender == null || character.gender.equals(
                            gender,
                            ignoreCase = true
                        )
                    // Проверяем соответствие фильтру по расе..
                    val matchesSpecies =
                        species == null || character.species.equals(
                            species,
                            ignoreCase = true
                        )

                    // Персонаж попадает в итоговый список, если соответствует всем условиям..
                    matchesSearchQuery && matchesStatus && matchesGender && matchesSpecies
                }
            // Возвращаем новый объект состояния UI с отфильтрованным списком..
            HomeScreenUiState(characters = filteredList)
        }
            // Превращаем "холодный" Flow в "горячий" StateFlow, который хранит последнее значение..
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // Начинает сбор, когда UI активен..
                initialValue = HomeScreenUiState(isLoading = true), // Начальное состояние - загрузка..
            )

    // Это старое состояние, которое, вероятно, больше не используется и может быть удалено..
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Загружаем свежие данные при инициализации ViewModel..
            refreshData()
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            // Запускаем обновление данных из сети; UI обновится автоматически благодаря Flow..
            ramRepo.refresh()
        }
    }

    // Обновляет значение поискового запроса, что автоматически запускает фильтрацию через combine..
    fun onSearchByNameQuerySubmitted(query: String) {
        _searchQuery.value = query
    }

    // Эта функция использует старый подход и, вероятно, может быть удалена..
    fun filterCharacters() {
        // ...
    }

    // Сбрасывает фильтры, устанавливая их значения в null..
    fun resetFilters() {
        _statusFilter.value = null
        _genderFilter.value = null
    }

    // Обновляет значение фильтра по статусу..
    fun statusFilterChanged(status: String?) {
        _statusFilter.value = if (status == "not selected") null else status
    }

    // Обновляет значение фильтра по полу..
    fun genderFilterChanged(gender: String?) {
        _genderFilter.value = if (gender == "not selected") null else gender
    }

    // Обновляет значение фильтра по расе..
    fun speciesFilterChanged(species: String?) {
        _speciesFilter.value = if (species == "not selected") null else species
    }
}
