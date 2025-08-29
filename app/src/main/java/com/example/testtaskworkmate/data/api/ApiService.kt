package com.example.testtaskworkmate.data.api

import com.example.testtaskworkmate.data.source.network.ApiResponse
import com.example.testtaskworkmate.data.source.network.NetworkCharacter
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Получение одного персонажа по его ID.
    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): NetworkCharacter

    // Получение списка персонажей.
    @GET("character")
    fun getCharacters(
        @Query("name")
        name: String? = null, // String? - значит, параметр может отсутствовать
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("gender") gender: String? = null,
        @Query("type") type: String? = null,
    ): Flow<ApiResponse>
}
