package com.example.movieapp.ui.moviedetail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MovieDetailsScreen(
    navController: NavController
) {
    Text(
        text = "Movie Details Screen",
        modifier = Modifier
    )
}