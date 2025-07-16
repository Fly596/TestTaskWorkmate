package com.example.testtaskworkmate.data.repos

import com.example.testtaskworkmate.data.model.Character
import com.example.testtaskworkmate.data.network.RamApi
import javax.inject.Inject
import javax.inject.Singleton

interface RamRepository {

    suspend fun getCharacters(): List<Character>
}

@Singleton
class RamRepositoryImpl @Inject constructor(private val ramApi: RamApi) :
    RamRepository {

    override suspend fun getCharacters(): List<Character> =
        ramApi.getCharacters().results
}
