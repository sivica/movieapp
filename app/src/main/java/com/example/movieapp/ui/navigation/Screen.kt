package com.example.movieapp.ui.navigation

sealed class Screen(val route: String) {
    data object List : Screen("movieList")
    data object Detail : Screen("movieDetail/{movieId}") {
        fun createRoute(movieId: Int) = "movieDetail/$movieId"
    }
}