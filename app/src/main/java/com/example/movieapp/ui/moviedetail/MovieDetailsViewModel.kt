package com.example.movieapp.ui.moviedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.MovieDetails
import com.example.movieapp.domain.usecase.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.movieapp.domain.util.Result
import kotlinx.coroutines.flow.update

data class MovieDetailsUiState(
    val isLoading: Boolean = false,
    val movieDetails: MovieDetails? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow(MovieDetailsUiState(isLoading = true))
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    init {
        fetchMovieDetails()
    }

    fun fetchMovieDetails() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            getMovieDetailsUseCase(movieId)
                .collect { result ->
                    _uiState.update { currentState ->
                        when (result) {
                            is Result.Success -> currentState.copy(
                                isLoading = false,
                                movieDetails = result.data,
                                errorMessage = null
                            )
                            is Result.Error -> currentState.copy(
                                isLoading = false,
                                movieDetails = null, // Clear data on error
                                errorMessage = result.exception.message ?: "An unknown error occurred"
                            )
                        }
                    }
                }
        }
    }

    // Add functions for other actions like handling favorite button clicks
}