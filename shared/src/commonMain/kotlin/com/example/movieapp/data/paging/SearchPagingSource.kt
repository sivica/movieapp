package com.example.movieapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.data.datasource.TmdbRemoteDataSource
import com.example.movieapp.data.mapper.toSearchResultItemDomain
import com.example.movieapp.domain.model.SearchResultItem
import com.example.movieapp.domain.util.Result

class SearchPagingSource(
    private val remoteDataSource: TmdbRemoteDataSource,
    private val query: String
) : PagingSource<Int, SearchResultItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResultItem> {
        val page = params.key ?: 1

        if (query.isBlank()) {
            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
        }

        val result = remoteDataSource.searchMovies(query, page)

        return when (result) {
            is Result.Success -> {
                val data = result.data
                val totalPages = data.totalPages

                val searchResults: List<SearchResultItem> =
                    data.results.map { it.toSearchResultItemDomain() }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (searchResults.isEmpty() || page >= totalPages) null else page + 1

                LoadResult.Page(
                    data = searchResults,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
            is Result.Error -> {
                LoadResult.Error(result.exception)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SearchResultItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
