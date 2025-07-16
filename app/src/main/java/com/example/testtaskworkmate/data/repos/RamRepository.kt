package com.example.testtaskworkmate.data.repos

import com.example.testtaskworkmate.data.source.local.CharacterDao
import com.example.testtaskworkmate.data.source.local.toEntity
import com.example.testtaskworkmate.data.source.local.toNetwork
import com.example.testtaskworkmate.data.source.network.NetworkCharacter
import com.example.testtaskworkmate.data.source.network.NetworkRepository
import javax.inject.Inject
import javax.inject.Singleton

interface RamRepository {

    suspend fun fetchCharacters(): List<NetworkCharacter>

    suspend fun refresh()

    suspend fun getCharacterById(id: Int): NetworkCharacter

    suspend fun getCharactersByName(name: String): List<NetworkCharacter>
}

@Singleton
class RamRepositoryImpl
@Inject
constructor(
    private val networkRepository: NetworkRepository,
    private val characterDao: CharacterDao,
) : RamRepository {

    override suspend fun fetchCharacters(): List<NetworkCharacter> {
        val localCharacters = characterDao.getAllCharacters()

        // Проверка наличия данных в локальной базе данных.
        if (localCharacters.isNotEmpty()) {
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
    }

    override suspend fun getCharactersByName(
        name: String
    ): List<NetworkCharacter> {
        return characterDao.findCharactersByName(name).toNetwork()
    }

    override suspend fun refresh() {
        val networkCharacters = networkRepository.getAllCharacters()
        characterDao.deleteAll()

        // Преобразуем список NetworkCharacter в список Entity локальной
        // базы данных.
        val characterEntities = networkCharacters.map { it.toEntity() }
        characterDao.insertCharacters(characterEntities)
    }

    override suspend fun getCharacterById(id: Int): NetworkCharacter {
        val localCharacter = characterDao.getCharacterById(id)
        if (localCharacter != null) {
            return localCharacter.toNetwork()
        }
        val networkCharacter = networkRepository.getCharacter(id)
        characterDao.insertCharacter(networkCharacter.toEntity())
        return networkCharacter
    }
}
