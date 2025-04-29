package com.example.movieapp.ui.navigation

import com.example.movieapp.ui.search.SearchScreen

sealed class Screen(val route: String) {
    data object List : Screen("movieList")
    data object Detail : Screen("movieDetail/{movieId}") {
        fun createRoute(movieId: Int) = "movieDetail/$movieId"
    }
    data object Search : Screen("search")
}