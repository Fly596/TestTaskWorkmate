package com.example.testtaskworkmate.data.source.network

import retrofit2.http.GET

interface NetworkDataSource {
    @GET("character")
    suspend fun getCharacters(): ApiResponse
}

