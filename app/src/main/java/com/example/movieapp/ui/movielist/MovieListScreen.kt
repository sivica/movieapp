@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.movieapp.ui.movielist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.ui.navigation.Screen

@Composable
fun MovieListScreen(
    navController: NavController,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val lazyMovieItems: LazyPagingItems<Movie> = viewModel.popularMoviesFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Popular Movies") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Search.route)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Movies"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    count = lazyMovieItems.itemCount,
                    key = lazyMovieItems.itemKey { it.id }
                ) { index ->
                    val movie = lazyMovieItems[index]
                    if (movie != null) {
                        MovieItem(
                            movie = movie,
                            onClick = { movieId ->
                                navController.navigate(Screen.Detail.createRoute(movieId))
                            }
                        )
                    }
                }

                lazyMovieItems.loadState.append.let { appendState ->
                    when (appendState) {
                        is LoadState.Loading -> {
                            item {
                                LoadingItemIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp)
                                )
                            }
                        }

                        is LoadState.Error -> {
                            item {
                                ErrorRetryItem(
                                    message = appendState.error.localizedMessage
                                        ?: "Error loading more movies",
                                    onRetryClick = { lazyMovieItems.retry() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                )
                            }
                        }

                        is LoadState.NotLoading -> Unit
                    }
                }
            }

            lazyMovieItems.loadState.refresh.let { refreshState ->
                when (refreshState) {
                    is LoadState.Loading -> {
                        if (lazyMovieItems.itemCount == 0) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    is LoadState.Error -> {
                        if (lazyMovieItems.itemCount == 0) {
                            ErrorRetryItem(
                                message = refreshState.error.localizedMessage
                                    ?: "Failed to load movies",
                                onRetryClick = { lazyMovieItems.retry() },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp)
                            )
                        }
                    }

                    is LoadState.NotLoading -> Unit
                }
            }
        }
    }
}


@Composable
fun LoadingItemIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorRetryItem(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetryClick) {
            Text("Retry")
        }
    }
}