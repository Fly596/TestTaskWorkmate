package com.example.testtaskworkmate.data.source.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(val info: Info, val results: List<NetworkCharacter>)

@Serializable
data class Info(
    val count: Int,
    val pages: Int, // 42.
    val next: String?,
    val prev: String?,
)

@Serializable
data class NetworkCharacter(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: CharacterLocation,
    val location: CharacterLocation,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String,
)

@Serializable
data class CharacterLocation(val name: String, val url: String)

enum class CharacterStatus(val statusValue: String) {
    Alive("alive"),
    Dead("dead"),
    Unknown("unknown"),
}

enum class CharacterGender(val genderValue: String) {
    Female("female"),
    Male("male"),
    Genderless("genderless"),
    Unknown("unknown")
}
