package com.example.testtaskworkmate.data.model

import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

interface RamRepository{
    @GET("character")
    suspend fun getCharacters():List<Character>
}

@Singleton
class RamRepositoryImpl @Inject constructor(): RamRepository{

    override suspend fun getCharacters(): List<Character> {
        TODO("Not yet implemented")
    }

}