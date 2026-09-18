package com.example.movieapp.data.remote

import com.example.movieapp.data.datasource.TmdbRemoteDataSource
import com.example.movieapp.data.repository.MovieRepositoryImpl
import com.example.movieapp.domain.repository.MovieRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Android adapter around [createTmdbHttpClient].
 *
 * Pass the TMDB v3 key from the app (`BuildConfig.TMDB_API_KEY`). Debug builds
 * keep OkHttp BODY logging, matching the previous Retrofit stack. The key is
 * never read from disk inside `:shared`.
 */
fun createAndroidTmdbHttpClient(
    apiKey: String,
    debugLogging: Boolean,
): HttpClient {
    val engine = OkHttp.create {
        config {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (debugLogging) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
            addInterceptor(loggingInterceptor)
        }
    }
    return createTmdbHttpClient(apiKey = apiKey, engine = engine)
}

fun createAndroidMovieRepository(
    apiKey: String,
    debugLogging: Boolean,
): MovieRepository {
    return MovieRepositoryImpl(
        TmdbRemoteDataSource(createAndroidTmdbHttpClient(apiKey, debugLogging))
    )
}
