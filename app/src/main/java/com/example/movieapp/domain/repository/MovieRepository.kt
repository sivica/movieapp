package com.example.movieapp.domain.repository

import androidx.paging.PagingData
import com.example.movieapp.domain.model.MediaType
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.MovieDetails
import com.example.movieapp.domain.model.SearchResultItem
import kotlinx.coroutines.flow.Flow
import com.example.movieapp.domain.util.Result

interface MovieRepository {
    fun getPopularMoviesStream(): Flow<PagingData<Movie>>
    fun searchMedia(query: String, mediaType: MediaType): Flow<PagingData<SearchResultItem>>
    fun getMovieDetails(id: Int): Flow<Result<MovieDetails>>
}