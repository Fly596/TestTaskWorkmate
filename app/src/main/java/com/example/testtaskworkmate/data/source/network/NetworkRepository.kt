package com.example.testtaskworkmate.data.source.network

import com.example.testtaskworkmate.data.api.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface NetworkRepository {

    fun getAllCharacters(): Flow<List<NetworkCharacter>>

    suspend fun getCharacter(id: Int): NetworkCharacter
}

class NetworkRepositoryImpl @Inject constructor(private val api: ApiService) :
    NetworkRepository {

    override fun getAllCharacters(): Flow<List<NetworkCharacter>> {
        return api.getCharacters().map { characters ->
            characters.results
        }
    }

    override suspend fun getCharacter(id: Int): NetworkCharacter {
        return api.getCharacter(id)
    }
}
