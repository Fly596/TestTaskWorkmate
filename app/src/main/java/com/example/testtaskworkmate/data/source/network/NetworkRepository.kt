package com.example.testtaskworkmate.data.source.network

import android.util.Log
import com.example.testtaskworkmate.data.api.ApiService
import javax.inject.Inject

interface NetworkRepository {

    suspend fun getAllCharacters(): List<NetworkCharacter>

    suspend fun getCharacter(id: Int): NetworkCharacter
}

class NetworkRepositoryImpl @Inject constructor(private val api: ApiService) :
    NetworkRepository {

    override suspend fun getAllCharacters(): List<NetworkCharacter> {
        val allCharacters = mutableListOf<NetworkCharacter>()
        var currentPage = 1
        var hasNextPage = true

        while (hasNextPage) {
            try {
                val response = api.getCharacters(page = currentPage)
                allCharacters.addAll(response.results)

                if (response.info.next != null) {
                    currentPage++
                } else {
                    hasNextPage = false
                }
            } catch (e: Exception) {
                hasNextPage = false
                Log.e("NetworkRepo", "Failed to fetch page $currentPage", e)
            }
        }
        return allCharacters
    }

    override suspend fun getCharacter(id: Int): NetworkCharacter {
        return api.getCharacter(id)
    }
}
