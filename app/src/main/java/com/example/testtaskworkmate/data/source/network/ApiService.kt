package com.example.testtaskworkmate.data.source.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("character")
    suspend fun getCharacters(
        @Query("name")
        name: String? = null, // String? - значит, параметр может отсутствовать
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("gender") gender: String? = null,
        @Query("type") type: String? = null,
    ): ApiResponse
}
