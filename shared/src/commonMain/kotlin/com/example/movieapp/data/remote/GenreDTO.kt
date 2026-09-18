package com.example.movieapp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class GenreDTO(
    val id: Int? = null,
    val name: String? = null
)
