package com.example.movieapp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class MovieListDTO(
    val page: Int,
    val results: List<MovieDTO>,
    val totalPages: Int,
    val totalResults: Int
)