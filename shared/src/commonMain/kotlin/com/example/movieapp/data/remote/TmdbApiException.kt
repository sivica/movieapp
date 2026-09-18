package com.example.movieapp.data.remote

class TmdbApiException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
