package com.example.movieapp.di

import com.example.movieapp.BuildConfig
import com.example.movieapp.data.remote.createAndroidMovieRepository
import com.example.movieapp.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMovieRepository(): MovieRepository {
        return createAndroidMovieRepository(
            apiKey = BuildConfig.TMDB_API_KEY,
            debugLogging = BuildConfig.DEBUG,
        )
    }
}
