package com.example.testtaskworkmate.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.example.testtaskworkmate.data.model.Character
import com.example.testtaskworkmate.data.repos.RamRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

sealed interface HomeScreenUiState {
    data class Success(val characters: List<Character>) : HomeScreenUiState

    object Error : HomeScreenUiState

    object Loading : HomeScreenUiState
}

@HiltViewModel
class HomeScreenViewModel
@Inject
constructor(private val ramRepo: RamRepositoryImpl) : ViewModel() {

    var homeUiState: HomeScreenUiState by
    mutableStateOf(HomeScreenUiState.Loading)
        private set

    init {
        // получение списка персонажей при инициализации ViewModel.
        getCharacters()
    }

    private fun getCharacters() {
        viewModelScope.launch {
            homeUiState = HomeScreenUiState.Loading
            homeUiState =
                try {
                    HomeScreenUiState.Success(ramRepo.getCharacters())
                } catch (e: IOException) {
                    HomeScreenUiState.Error
                } catch (e: HttpException) {
                    HomeScreenUiState.Error
                }
        }
    }
}
