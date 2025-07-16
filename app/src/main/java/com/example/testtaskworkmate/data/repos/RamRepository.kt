package com.example.testtaskworkmate.data.repos

import com.example.testtaskworkmate.data.model.Character
import com.example.testtaskworkmate.data.network.RamApi
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class RamRepository @Inject constructor(
    private val ramApi: RamApi
) {
    suspend fun getCharacters(): List<Character>{
        return ramApi.getCharacters()
    }
}
