package com.example.movieapp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val BASE_POSTER_IMAGE_URL = "https://image.tmdb.org/t/p/w500/"
    }

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1
    ) : Response<MovieListDTO>
}