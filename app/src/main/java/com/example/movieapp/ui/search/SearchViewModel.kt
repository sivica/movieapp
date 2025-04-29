package com.example.movieapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movieapp.domain.model.MediaType
import com.example.movieapp.domain.model.SearchResultItem
import com.example.movieapp.domain.usecase.SearchMediaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val selectedMediaType: MediaType = MediaType.MOVIE
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMediaUseCase: SearchMediaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val searchParams = _uiState
        .map { Pair(it.query, it.selectedMediaType) }
        .distinctUntilChanged()

    val searchResultsFlow: Flow<PagingData<SearchResultItem>> = searchParams
        .debounce(500)
        .flatMapLatest { (query, mediaType) ->
            if (query.isBlank()) {
                flowOf(PagingData.empty())
            } else {
                searchMediaUseCase(query, mediaType)
            }
        }
        .cachedIn(viewModelScope)

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun onMediaTypeSelected(mediaType: MediaType) {
        _uiState.update { it.copy(selectedMediaType = mediaType) }
    }
}