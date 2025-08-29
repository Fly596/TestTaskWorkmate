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

// Класс данных, представляющий состояние UI для экрана с деталями..
data class DetailsUiState(
    // Хранит данные о персонаже; null, пока они не загружены..
    val character: NetworkCharacter? = null,
    // Флаг, указывающий, идет ли в данный момент загрузка данных..
    val isLoading: Boolean = false,
    // Хранит сообщение об ошибке, если она произошла..
    val error: String? = null,
)

@HiltViewModel
// ViewModel для экрана с деталями персонажа..
class DetailsViewModel
@Inject
constructor(
    // Репозиторий для получения данных о персонажах..
    private val ramRepo: RamRepository,
    // Обработчик сохраненного состояния для получения аргументов навигации (ID персонажа)..
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Приватное, изменяемое состояние UI..
    private val _uiState = MutableStateFlow(DetailsUiState())

    // Публичное, неизменяемое состояние UI, на которое подписывается экран..
    val uiState = _uiState.asStateFlow()

    // Блок инициализации, который выполняется при создании ViewModel..
    init {
        // Запускаем корутину в жизненном цикле ViewModel..
        viewModelScope.launch {
            // Устанавливаем состояние загрузки в true..
            _uiState.update { it.copy(isLoading = true) }

            // Переключаемся на фоновый поток для работы с данными..
            withContext(Dispatchers.IO) {
                try {
                    // Получаем аргументы навигации с помощью type-safe метода toRoute..
                    val args = savedStateHandle.toRoute<Details>()
                    // Извлекаем ID персонажа из аргументов..
                    val characterId = args.id
                    // Вызываем функцию для загрузки данных по этому ID..
                    getCharacter(characterId)
                } catch (e: Exception) {
                    // Выводим ошибку в лог, если не удалось получить аргументы..
                    e.printStackTrace()
                }
                // Устанавливаем состояние загрузки в false после завершения операции..
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Приватная функция для получения данных одного персонажа..
    private fun getCharacter(id: Int) {
        viewModelScope.launch {
            try {
                // Запрашиваем данные персонажа из репозитория..
                val character = ramRepo.getCharacterById(id)
                // Обновляем состояние UI полученными данными..
                _uiState.update { it.copy(character = character) }
            } catch (e: Exception) {
                // Выводим ошибку в лог, если загрузка не удалась..
                e.printStackTrace()
            }
        }
    }
}