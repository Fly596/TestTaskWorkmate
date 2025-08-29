package com.example.testtaskworkmate.data.repos

import com.example.testtaskworkmate.data.source.local.CharacterDao
import com.example.testtaskworkmate.data.source.local.CharacterFilters
import com.example.testtaskworkmate.data.source.local.LocalCharacter
import com.example.testtaskworkmate.data.source.local.toEntity
import com.example.testtaskworkmate.data.source.local.toNetwork
import com.example.testtaskworkmate.data.source.network.NetworkCharacter
import com.example.testtaskworkmate.data.source.network.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface RamRepository {

    // Получение списка персонажей.
    fun fetchCharacters(): Flow<List<NetworkCharacter>>

    // Обновление данных.
    suspend fun refresh()

    // Получение одного персонажа по его ID.
    suspend fun getCharacterById(id: Int): NetworkCharacter?

    suspend fun getCharactersByName(name: String): List<NetworkCharacter>

    fun getFilteredCharacters(
        filters: CharacterFilters
    ): Flow<List<NetworkCharacter>>
}

@Singleton
class RamRepositoryImpl
@Inject
constructor(
    private val networkRepository: NetworkRepository,
    private val characterDao: CharacterDao,
) : RamRepository {

    override fun fetchCharacters(): Flow<List<NetworkCharacter>> {

        val localCharacters = characterDao.getAllCharacters()

        // Проверка наличия данных в локальной базе данных.
        if (localCharacters.isNotEmpty()) {
            return characterDao.getAllCharactersFlow().map { it.toNetwork() }
        } else {
            // Если данных нет, делаем запрос к API и сохраняем в локальную базу
            // данных.
            val networkCharacters = networkRepository.getAllCharacters()

            // Преобразуем список NetworkCharacter в список Entity локальной
            // базы данных.
            var charactersList: List<LocalCharacter>
            val characterEntitiesFlow = networkCharacters.map { characters ->
                charactersList = characters.map { it.toEntity() }
                characters.map { it.toEntity() }
            }


            // Добавляем список Entity в локальную базу данных.
            characterDao.insertCharacters(charactersList)

            // Получаем обновленные данные из локальной базы данных.
            val newLocalCharacters = characterDao.getAllCharactersFlow()

            // Возвращаем список NetworkCharacter.
            return newLocalCharacters.toNetwork()
        }
    }

    /*     override fun fetchCharacters():Flow<List<NetworkCharacter>> {
        val localCharactersFlow = characterDao.getAllCharacters()
        val localCharacters = localCharactersFlow.map { it.toNetwork() }.collect {
            return it
        }

        // Проверка наличия данных в локальной базе данных.
        if (localCharacters.is) {
            return localCharacters.toNetwork()
        } else {
            // Если данных нет, делаем запрос к API и сохраняем в локальную базу
            // данных.
            val networkCharacters = networkRepository.getAllCharacters()

            // Преобразуем список NetworkCharacter в список Entity локальной
            // базы данных.
            val characterEntities = networkCharacters.map { it.toEntity() }

            // Добавляем список Entity в локальную базу данных.
            characterDao.insertCharacters(characterEntities)

            // Получаем обновленные данные из локальной базы данных.
            val newLocalCharacters = characterDao.getAllCharacters()

            // Возвращаем список NetworkCharacter.
            return newLocalCharacters.toNetwork()
        }
    } */

    override suspend fun getCharactersByName(
        name: String
    ): List<NetworkCharacter> {
        return characterDao.findCharactersByName(name).toNetwork()
    }

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
            .toNetwork()
    }

    // Обновляет данные из интернета.
    override suspend fun refresh() {
        // Запрос данных.
        val networkCharacters = networkRepository.getAllCharacters()
        // Очистка локальной бд.
        characterDao.deleteAll()

        // Преобразуем список NetworkCharacter в список Entity локальной
        // базы данных.
        val characterEntities = networkCharacters.map { it.toEntity() }

        characterDao.insertCharacters(characterEntities)
    }

    override suspend fun getCharacterById(id: Int): NetworkCharacter? {
        val localCharacter = characterDao.getCharacterById(id)

        if (localCharacter != null) {
            return localCharacter.toNetwork()
        } else {
            // Объекта нет в локальной БД, поэтому поулчаем данные из API.
            val networkCharacter = networkRepository.getCharacter(id)
            characterDao.insertCharacter(networkCharacter.toEntity())
            return networkCharacter
        }
    }
}
