package com.example.movieapp.domain.model

data class SearchResultItem(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val mediaType: MediaType
)

enum class MediaType { MOVIE, TV }
