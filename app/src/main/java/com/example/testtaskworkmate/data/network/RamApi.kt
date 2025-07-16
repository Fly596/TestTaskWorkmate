package com.example.testtaskworkmate.data.network

import com.example.testtaskworkmate.data.model.Character
import retrofit2.http.GET

interface RamApi{
    @GET("character")
    suspend fun getCharacters():List<Character>
}

