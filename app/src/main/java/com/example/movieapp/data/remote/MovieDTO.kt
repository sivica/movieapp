package com.example.movieapp.data.remote

import com.example.movieapp.domain.model.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDTO(
    val id: Int,
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    val title: String,
)

fun MovieDTO.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath?.let { "${TmdbApiService.BASE_POSTER_IMAGE_URL}$it" },
        releaseYear = releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4)
    )
}