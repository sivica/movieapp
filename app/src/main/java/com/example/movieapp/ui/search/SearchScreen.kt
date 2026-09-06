@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.movieapp.ui.search

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.movieapp.domain.model.SearchResultItem
import com.example.movieapp.ui.movielist.ErrorRetryItem
import com.example.movieapp.ui.movielist.LoadingItemIndicator
import com.example.movieapp.ui.navigation.Screen

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazySearchResults: LazyPagingItems<SearchResultItem> =
        viewModel.searchResultsFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search movies") },
                singleLine = true,
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(
                        count = lazySearchResults.itemCount,
                        key = lazySearchResults.itemKey { "${it.mediaType}-${it.id}" }
                    ) { index ->
                        val item = lazySearchResults[index]
                        if (item != null) {
                            SearchResultListItem(
                                item = item,
                                onClick = { selectedItem ->
                                    navController.navigate(Screen.Detail.createRoute(selectedItem.id))
                                }
                            )
                        }
                    }

                    lazySearchResults.loadState.append.let { state ->
                        when (state) {
                            is LoadState.Loading -> item { LoadingItemIndicator(Modifier.fillMaxWidth().padding(vertical = 16.dp)) }
                            is LoadState.Error -> item {
                                ErrorRetryItem(
                                    message = state.error.localizedMessage ?: "Error loading more results",
                                    onRetryClick = { lazySearchResults.retry() },
                                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                                )
                            }
                            else -> Unit
                        }
                    }
                }


                val refreshState = lazySearchResults.loadState.refresh
                val isListEmpty = lazySearchResults.itemCount == 0

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when {
                        refreshState is LoadState.Loading && isListEmpty -> {
                            CircularProgressIndicator()
                        }
                        refreshState is LoadState.Error && isListEmpty -> {
                            ErrorRetryItem(
                                message = refreshState.error.localizedMessage ?: "Failed to load search results",
                                onRetryClick = { lazySearchResults.retry() },
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        refreshState is LoadState.NotLoading && isListEmpty && uiState.query.isNotBlank() -> {
                            Text("No results found for \"${uiState.query}\"")
                        }
                        refreshState is LoadState.NotLoading && isListEmpty && uiState.query.isBlank() -> {
                            Text("Enter a query to search")
                        }
                    }
                }
            }
        }
    }
}
