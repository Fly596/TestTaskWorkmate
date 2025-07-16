package com.example.testtaskworkmate.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<LocalCharacters>)

    @Query("SELECT * FROM characters")
    suspend fun getAllCharacters(): List<LocalCharacters>
}
