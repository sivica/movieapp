package com.example.movieapp.data.mapper

import com.example.movieapp.data.remote.MovieDTO
import com.example.movieapp.data.remote.MovieDetailsDTO
import com.example.movieapp.data.remote.TmdbConfig
import com.example.movieapp.domain.model.MediaType
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.MovieDetails
import com.example.movieapp.domain.model.SearchResultItem

fun MovieDTO.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath?.let { "${TmdbConfig.BASE_POSTER_IMAGE_URL}$it" },
        releaseYear = releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4)
    )
}

fun MovieDetailsDTO.toDomain(): MovieDetails? {
    if (id == null || title == null) return null
    return MovieDetails(
        id = id,
        title = title,
        overview = overview ?: "",
        posterUrl = posterPath?.let { "${TmdbConfig.BASE_POSTER_IMAGE_URL}$it" },
        releaseYear = releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4),
        rating = voteAverage ?: 0.0,
        genres = genres?.mapNotNull { it.name } ?: emptyList(),
        runtimeMinutes = runtime
    )
}

fun MovieDTO.toSearchResultItemDomain(): SearchResultItem {
    return SearchResultItem(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath?.let { "${TmdbConfig.BASE_POSTER_IMAGE_URL}$it" },
        mediaType = MediaType.MOVIE
    )
}
