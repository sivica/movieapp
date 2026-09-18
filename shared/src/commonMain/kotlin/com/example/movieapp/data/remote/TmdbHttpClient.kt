package com.example.movieapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Builds a TMDB Ktor client for [commonMain].
 *
 * The v3 API key is a constructor argument so this module never reads
 * `local.properties`, environment variables, or BuildConfig. The Android app
 * loads `BuildConfig.TMDB_API_KEY` (from `tmdb_api_key` in local.properties or
 * the `TMDB_API_KEY` env var) and passes it here. `createAndroidTmdbHttpClient`
 * is the Android OkHttp adapter.
 */
fun createTmdbHttpClient(
    apiKey: String,
    engine: HttpClientEngine,
    json: Json = tmdbJson,
): HttpClient = HttpClient(engine) {
    expectSuccess = false
    install(ContentNegotiation) {
        json(json)
    }
    defaultRequest {
        url.takeFrom(TmdbConfig.BASE_URL)
        url.parameters.append("api_key", apiKey)
    }
}

internal val tmdbJson: Json = Json {
    ignoreUnknownKeys = true
}
