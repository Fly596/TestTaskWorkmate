package com.example.testtaskworkmate.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.testtaskworkmate.data.source.network.CharacterLocation
import com.example.testtaskworkmate.data.source.network.NetworkCharacter

data class CharacterFilters(
    val name: String? = null,
    val status: String? = null,
    val genders: String? = null,
    val species: String? = null,
    val types: String? = null,
)

@Entity(tableName = "characters")
@TypeConverters(Converters::class)
data class LocalCharacter(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val originName: String,
    val originUrl: String,
    val locationName: String,
    val locationUrl: String,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String,
)

class Converters {

    @TypeConverter
    fun fromString(value: String): List<String> = value.split(',')

    @TypeConverter
    fun fromList(list: List<String>): String = list.joinToString(",")
}

// Функция для преобразования Character в CharactersEntity.
fun NetworkCharacter.toEntity(): LocalCharacter {
    return LocalCharacter(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        type = this.type,
        gender = this.gender,
        originName = this.origin.name,
        originUrl = this.origin.url,
        locationName = this.location.name,
        locationUrl = this.location.url,
        image = this.image,
        episode = this.episode,
        url = this.url,
        created = this.created,
    )
}

fun LocalCharacter.toNetwork(): NetworkCharacter {
    return NetworkCharacter(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        type = this.type,
        gender = this.gender,
        origin = CharacterLocation(this.originName, this.originUrl),
        location = CharacterLocation(this.locationName, this.locationUrl),
        image = this.image,
        episode = this.episode,
        url = this.url,
        created = this.created,
    )
}

fun List<LocalCharacter>.toNetwork(): List<NetworkCharacter> {
    return this.map { it.toNetwork() }
}
