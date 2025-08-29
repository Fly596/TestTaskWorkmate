package com.example.testtaskworkmate.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<LocalCharacter>)

    @Query("DELETE FROM characters")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: LocalCharacter)

    @Query("SELECT * FROM characters")
    fun getAllCharactersFlow(): Flow<List<LocalCharacter>>

    @Query("SELECT * FROM characters")
    fun getAllCharacters(): List<LocalCharacter>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Int): LocalCharacter?

    @Query("SELECT * FROM characters WHERE name LIKE '%' || :name || '%'")
    suspend fun findCharactersByName(name: String): List<LocalCharacter>

    @Query(
        """
        SELECT * FROM characters 
        WHERE 
            (:name IS NULL OR name LIKE '%' || :name || '%') AND
            (:statuses IS NULL OR status =:statuses) AND
            (:genders IS NULL OR gender =:genders) AND
            (:species IS NULL OR species =:species) AND
            (:type IS NULL OR type = :type)
    """
    )
    fun getFilteredCharacters(
        name: String?,
        statuses: String?,
        genders: String?,
        species: String?,
        type: String?,
    ): Flow<List<LocalCharacter>>
}
