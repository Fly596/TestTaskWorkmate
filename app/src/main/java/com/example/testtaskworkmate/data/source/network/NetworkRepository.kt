package com.example.testtaskworkmate.data.source.network

import com.example.testtaskworkmate.data.api.ApiService
import javax.inject.Inject

interface NetworkRepository {

    suspend fun getAllCharacters(): List<NetworkCharacter>

    suspend fun getCharacter(id: Int): NetworkCharacter
}

class NetworkRepositoryImpl @Inject constructor(private val api: ApiService) :
    NetworkRepository {

    override suspend fun getAllCharacters(): List<NetworkCharacter> =
        api.getCharacters().results

    override suspend fun getCharacter(id: Int): NetworkCharacter {
        return api.getCharacter(id)
    }
}
