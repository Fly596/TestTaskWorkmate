package com.example.testtaskworkmate.data.source.network

import android.util.Log
import com.example.testtaskworkmate.data.api.ApiService
import javax.inject.Inject

// Интерфейс для слоя, отвечающего исключительно за получение данных из сети..
interface NetworkRepository {

    // Асинхронно загружает список всех персонажей..
    suspend fun getAllCharacters(): List<NetworkCharacter>

    // Асинхронно загружает одного персонажа по его ID..
    suspend fun getCharacter(id: Int): NetworkCharacter
}

// Реализация сетевого репозитория..
class NetworkRepositoryImpl @Inject constructor(private val api: ApiService) :
    NetworkRepository {

    // Загружает персонажей со всех страниц API..
    override suspend fun getAllCharacters(): List<NetworkCharacter> {
        // Создаем изменяемый список для накопления результатов..
        val allCharacters = mutableListOf<NetworkCharacter>()
        var currentPage = 1
        var hasNextPage = true

        // Цикл для постраничной загрузки данных..
        while (hasNextPage) {
            try {
                // Выполняем сетевой запрос для текущей страницы..
                val response = api.getCharacters(page = currentPage)
                // Добавляем полученных персонажей в общий список..
                allCharacters.addAll(response.results)

                // Проверяем, есть ли следующая страница..
                if (response.info.next != null) {
                    currentPage++
                } else {
                    // Если следующей страницы нет, выходим из цикла..
                    hasNextPage = false
                }
            } catch (e: Exception) {
                // В случае ошибки (например, нет интернета) прекращаем загрузку..
                hasNextPage = false
                Log.e("NetworkRepo", "Failed to fetch page $currentPage", e)
            }
        }
        // Возвращаем полный список персонажей..
        return allCharacters
    }

    // Просто вызывает соответствующий метод API..
    override suspend fun getCharacter(id: Int): NetworkCharacter {
        return api.getCharacter(id)
    }
}