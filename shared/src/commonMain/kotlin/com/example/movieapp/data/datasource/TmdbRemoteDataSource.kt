package com.example.movieapp.data.datasource

import com.example.movieapp.data.remote.MovieDetailsDTO
import com.example.movieapp.data.remote.MovieListDTO
import com.example.movieapp.data.remote.TmdbApiException
import com.example.movieapp.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlin.coroutines.cancellation.CancellationException

class TmdbRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun getPopularMovies(page: Int): Result<MovieListDTO> {
        return safeApiCall {
            httpClient.get("movie/popular") {
                parameter("page", page)
            }
        }
    }

    suspend fun getMovieDetails(id: Int): Result<MovieDetailsDTO> {
        return safeApiCall {
            httpClient.get("movie/$id")
        }
    }

    suspend fun searchMovies(query: String, page: Int): Result<MovieListDTO> {
        return safeApiCall {
            httpClient.get("search/movie") {
                parameter("query", query)
                parameter("page", page)
            }
        }
    }

    private suspend inline fun <reified T> safeApiCall(
        crossinline apiCall: suspend () -> HttpResponse,
    ): Result<T> {
        return try {
            val response = apiCall()
            if (response.status.isSuccess()) {
                Result.Success(response.body())
            } else {
                Result.Error(TmdbApiException("HTTP ${response.status.value}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
