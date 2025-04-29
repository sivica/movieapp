package com.example.movieapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailsDTO(
    val id: Int?,
    val title: String?,
    val overview: String?,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("release_date") val releaseDate: String?,
    @SerialName("vote_average") val voteAverage: Double?,
    val genres: List<GenreDTO>?,
    val runtime: Int?
)