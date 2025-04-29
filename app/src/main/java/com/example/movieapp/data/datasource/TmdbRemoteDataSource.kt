package com.example.movieapp.data.datasource

import com.example.movieapp.data.remote.MovieDetailsDTO
import com.example.movieapp.data.remote.MovieListDTO
import com.example.movieapp.data.remote.TmdbApiService
import com.example.movieapp.domain.util.Result
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

interface TmdbRemoteDataSource {
    suspend fun getPopularMovies(page: Int): Result<MovieListDTO>
    suspend fun getMovieDetails(id: Int): Result<MovieDetailsDTO>
    suspend fun searchMovies(query: String, page: Int): Result<MovieListDTO>

}

class TmdbRemoteDataSourceImpl @Inject constructor(
    private val tmdbApi: TmdbApiService
) : TmdbRemoteDataSource {

    // Helper function to safely execute API calls and wrap results
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(Exception("API returned successful but empty response body"))
                }
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getPopularMovies(page: Int): Result<MovieListDTO> {
        return safeApiCall { tmdbApi.getPopularMovies(page) }
    }

    override suspend fun getMovieDetails(id: Int): Result<MovieDetailsDTO> {
        return safeApiCall { tmdbApi.getMovieDetails(id) }
    }

    override suspend fun searchMovies(query: String, page: Int): Result<MovieListDTO> {
        return safeApiCall { tmdbApi.searchMovies(query, page) }
    }
}