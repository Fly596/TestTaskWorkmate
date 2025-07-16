package com.example.testtaskworkmate.data.repos

import com.example.testtaskworkmate.data.source.local.CharacterDao
import com.example.testtaskworkmate.data.source.local.toEntity
import com.example.testtaskworkmate.data.source.local.toNetwork
import com.example.testtaskworkmate.data.source.network.NetworkCharacter
import com.example.testtaskworkmate.data.source.network.NetworkDataSource
import javax.inject.Inject
import javax.inject.Singleton

interface RamRepository {

    suspend fun getFilteredNetworkCharacters(
        name: String?,
        status: String?,
        species: String?,
        gender: String?,
        type: String?,
    ): List<NetworkCharacter>

    suspend fun getNetworkCharacters(): List<NetworkCharacter>

    suspend fun findCharactersByName(name: String): List<NetworkCharacter>
}

@Singleton
class RamRepositoryImpl
@Inject
constructor(
    private val networkDataSource: NetworkDataSource,
    private val characterDao: CharacterDao,
) : RamRepository {

    override suspend fun getNetworkCharacters(): List<NetworkCharacter> {
        val localCharacters = characterDao.getAllCharacters()

        // Проверка наличия данных в локальной базе данных.
        if (localCharacters.isNotEmpty()) {
            return localCharacters.toNetwork()
        } else {

            // Если данных нет, делаем запрос к API и сохраняем в локальную базу
            // данных.
            val networkCharacters = networkDataSource.getCharacters().results

            val characterEntities = networkCharacters.map { it.toEntity() }

            characterDao.insertCharacters(characterEntities)

            // Получаем обновленные данные из локальной базы данных.
            val newLocalCharacters = characterDao.getAllCharacters()

            // Возвращаем список NetworkCharacter.
            return newLocalCharacters.toNetwork()
        }
    }

    override suspend fun getFilteredNetworkCharacters(
        name: String?,
        status: String?,
        species: String?,
        gender: String?,
        type: String?,
    ): List<NetworkCharacter> {

        val networkCharacters =
            networkDataSource
                .getCharacters(name, status, species, gender, type)
                .results

        val characterEntities = networkCharacters.map { it.toEntity() }

        return characterEntities.toNetwork()
    }

    override suspend fun findCharactersByName(
        name: String
    ): List<NetworkCharacter> {
        return characterDao.findCharactersByName(name).toNetwork()
    }
}
