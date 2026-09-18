package com.example.movieapp.di

import com.example.movieapp.domain.repository.MovieRepository
import com.example.movieapp.domain.usecase.GetMovieDetailsUseCase
import com.example.movieapp.domain.usecase.GetPopularMoviesUseCase
import com.example.movieapp.domain.usecase.SearchMediaUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideGetPopularMoviesUseCase(
        repository: MovieRepository,
    ): GetPopularMoviesUseCase = GetPopularMoviesUseCase(repository)

    @Provides
    fun provideSearchMediaUseCase(
        repository: MovieRepository,
    ): SearchMediaUseCase = SearchMediaUseCase(repository)

    @Provides
    fun provideGetMovieDetailsUseCase(
        repository: MovieRepository,
    ): GetMovieDetailsUseCase = GetMovieDetailsUseCase(repository)
}
