package com.example.movieapp.data.remote

import com.example.movieapp.domain.model.Movie
import kotlinx.serialization.Serializable

@Serializable
data class MovieDTO(
    val adult: Boolean,
    val backdropPath: String,
    val genreIds: List<Int>,
    val id: Int,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val releaseDate: String,
    val title: String,
    val video: Boolean,
    val voteAverage: Double,
    val voteCount: Int
)

fun MovieDTO.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath.let { "${TmdbApiService.BASE_URL}$it" },
        releaseYear = releaseDate.takeIf { it.length >= 4 }?.substring(0, 4)
    )
}