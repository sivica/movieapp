package com.example.movieapp.ui.movielist

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MovieListScreen(
    navController: NavController
) {
    Text(
        text = "Movie List Screen",
        modifier = Modifier
    )
}