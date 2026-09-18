package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.MovieDetails
import com.example.movieapp.domain.repository.MovieRepository
import com.example.movieapp.domain.util.Result
import kotlinx.coroutines.flow.Flow

class GetMovieDetailsUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(id: Int): Flow<Result<MovieDetails>> {
        return repository.getMovieDetails(id)
    }
}
