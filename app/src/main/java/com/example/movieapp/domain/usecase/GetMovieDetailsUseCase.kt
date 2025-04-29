package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.MovieDetails
import com.example.movieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.example.movieapp.domain.util.Result

class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(id: Int): Flow<Result<MovieDetails>> {
        return repository.getMovieDetails(id)
    }
}