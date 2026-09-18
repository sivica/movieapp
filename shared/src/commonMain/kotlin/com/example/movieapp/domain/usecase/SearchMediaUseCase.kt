package com.example.movieapp.domain.usecase

import androidx.paging.PagingData
import com.example.movieapp.domain.model.MediaType
import com.example.movieapp.domain.model.SearchResultItem
import com.example.movieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class SearchMediaUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(query: String, mediaType: MediaType): Flow<PagingData<SearchResultItem>> {
        return repository.searchMedia(query.trim(), mediaType)
    }
}
