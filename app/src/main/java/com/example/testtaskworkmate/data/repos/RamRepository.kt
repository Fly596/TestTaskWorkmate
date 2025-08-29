package com.example.testtaskworkmate.data.repos

import android.util.Log
import com.example.testtaskworkmate.data.source.local.CharacterDao
import com.example.testtaskworkmate.data.source.local.CharacterFilters
import com.example.testtaskworkmate.data.source.local.toEntity
import com.example.testtaskworkmate.data.source.local.toNetwork
import com.example.testtaskworkmate.data.source.network.NetworkCharacter
import com.example.testtaskworkmate.data.source.network.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Интерфейс основного репозитория, который является "дирижером" между данными из сети и базой данных..
interface RamRepository {

    // Получает Flow персонажей из локальной базы данных..
    fun getCharactersFlow(): Flow<List<NetworkCharacter>>

    // Запускает принудительное обновление данных: загружает из сети и сохраняет в базу данных..
    suspend fun refresh()

    // Получает одного персонажа по его ID, используя кэш (БД) или сеть..
    suspend fun getCharacterById(id: Int): NetworkCharacter?

    // Получает список персонажей по имени из локальной базы данных..
    suspend fun getCharactersByName(name: String): List<NetworkCharacter>

    // Получает отфильтрованный Flow персонажей из локальной базы данных..
    fun getFilteredCharacters(
        filters: CharacterFilters
    ): Flow<List<NetworkCharacter>>
}

@Singleton
// Реализация основного репозитория..
class RamRepositoryImpl
@Inject
constructor(
    // Зависимость от сетевого репозитория для получения данных из интернета..
    private val networkRepository: NetworkRepository,
    // Зависимость от DAO для работы с локальной базой данных..
    private val characterDao: CharacterDao,
) : RamRepository {

    // Реализует "единый источник правды": отдает Flow прямо из DAO..
    override fun getCharactersFlow(): Flow<List<NetworkCharacter>> {
        // Слушаем изменения в базе данных и преобразуем (маппим) модели БД в сетевые модели для UI..
        return characterDao.getAllCharactersFlow().map { localCharacters ->
            localCharacters.toNetwork()
        }
    }

    // Ищет персонажей в локальной базе данных по имени..
    override suspend fun getCharactersByName(
        name: String
    ): List<NetworkCharacter> {
        return characterDao.findCharactersByName(name).toNetwork()
    }

    // Получает отфильтрованный список из DAO и маппит результат..
    override fun getFilteredCharacters(
        filters: CharacterFilters
    ): Flow<List<NetworkCharacter>> {
        return characterDao
            .getFilteredCharacters(
                name = filters.name,
                statuses = filters.status,
                genders = filters.genders,
                species = filters.species,
                type = filters.types,
            )
            .map { it.toNetwork() }
    }

    // Обновляет данные: загружает из сети и сохраняет в локальную базу данных..
    override suspend fun refresh() {
        try {
            // Загружаем полный список персонажей из сети..
            val networkCharacters = networkRepository.getAllCharacters()
            // Преобразуем сетевые модели в сущности для базы данных..
            val localCharacters = networkCharacters.map { it.toEntity() }
            // Полностью очищаем таблицу перед вставкой новых данных..
            characterDao.deleteAll()
            // Вставляем свежие данные в базу..
            characterDao.insertCharacters(localCharacters)
        } catch (e: Exception) {
            // Логируем ошибку, если во время обновления что-то пошло не так (например, нет сети)..
            Log.e("RamRepository", "Failed to refresh characters", e)
        }
    }

    // Получает персонажа по ID, сначала проверяя кэш (БД)..
    override suspend fun getCharacterById(id: Int): NetworkCharacter? {
        // Пытаемся найти персонажа в локальной базе данных..
        val localCharacter = characterDao.getCharacterById(id)

        // Если персонаж найден в БД, возвращаем его..
        if (localCharacter != null) {
            return localCharacter.toNetwork()
        } else {
            // Если в БД персонажа нет, загружаем его из сети..
            val networkCharacter = networkRepository.getCharacter(id)
            // Сохраняем загруженного персонажа в БД для будущего использования..
            characterDao.insertCharacter(networkCharacter.toEntity())
            // Возвращаем персонажа..
            return networkCharacter
        }
    }
}
