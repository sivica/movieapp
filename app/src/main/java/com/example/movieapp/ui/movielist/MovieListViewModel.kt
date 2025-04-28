package com.example.movieapp.ui.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.GetPopularMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    val popularMoviesFlow: Flow<PagingData<Movie>> =
        getPopularMoviesUseCase()
            .cachedIn(viewModelScope)
}