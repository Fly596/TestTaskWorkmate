package com.example.testtaskworkmate.data.repos

import com.example.testtaskworkmate.data.source.local.CharacterDao
import com.example.testtaskworkmate.data.source.local.toEntity
import com.example.testtaskworkmate.data.source.network.NetworkCharacter
import com.example.testtaskworkmate.data.source.network.NetworkDataSource
import javax.inject.Inject
import javax.inject.Singleton

interface RamRepository {

    suspend fun getCharacters(): List<NetworkCharacter>
}

@Singleton
class RamRepositoryImpl
@Inject
constructor(
    private val networkDataSource: NetworkDataSource,
    private val characterDao: CharacterDao,
) : RamRepository {

    override suspend fun getCharacters(): List<NetworkCharacter> {
        val networkCharacters = networkDataSource.getCharacters().results

        val characterEntities = networkCharacters.map { it.toEntity() }

        characterDao.insertCharacters(characterEntities)

        return networkCharacters
    }
}
