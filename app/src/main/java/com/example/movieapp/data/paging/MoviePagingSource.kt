package com.example.movieapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.data.datasource.TmdbRemoteDataSource
import com.example.movieapp.data.mapper.toDomain
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.util.Result

class MoviePagingSource(
    private val remoteDataSource: TmdbRemoteDataSource,
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1

        return when (val result = remoteDataSource.getPopularMovies(page)) {
            is Result.Success -> {
                val movieDTOs = result.data.results
                val domainMovies = movieDTOs.map { it.toDomain() }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (domainMovies.isEmpty() || result.data.page == result.data.totalPages) null else page + 1

                LoadResult.Page(
                    data = domainMovies,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
            is Result.Error -> {
                LoadResult.Error(result.exception)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}