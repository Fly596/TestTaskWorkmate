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

    @Query("SELECT * FROM characters WHERE name LIKE :name")
    suspend fun findCharactersByName(name: String): List<LocalCharacters>

    @Query(
        """
        SELECT * FROM characters 
        WHERE 
            (:name IS NULL OR name LIKE '%' || :name || '%') AND
            (:statuses IS NULL OR status IN (:statuses)) AND
            (:genders IS NULL OR gender IN (:genders)) AND
            (:species IS NULL OR species IN (:species))
    """
    )
    suspend fun getFilteredCharacters(
        name: String?,
        statuses: List<String>?,
        genders: List<String>?,
        species: List<String>?,
    ): List<LocalCharacters>
}
