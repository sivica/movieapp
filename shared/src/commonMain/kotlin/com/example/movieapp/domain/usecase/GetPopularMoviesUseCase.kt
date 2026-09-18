package com.example.movieapp.domain.usecase

import androidx.paging.PagingData
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetPopularMoviesUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(): Flow<PagingData<Movie>> {
        return repository.getPopularMoviesStream()
    }
}
