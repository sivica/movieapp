package com.example.movieapp.domain.model

data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val releaseYear: String?,
    val rating: Double,
    val genres: List<String>,
    val runtimeMinutes: Int?
)
