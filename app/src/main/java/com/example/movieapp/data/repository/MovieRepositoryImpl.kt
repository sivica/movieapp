package com.example.movieapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.data.datasource.TmdbRemoteDataSource
import com.example.movieapp.data.mapper.toDomain
import com.example.movieapp.data.paging.MoviePagingSource
import com.example.movieapp.domain.model.MediaType
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.MovieDetails
import com.example.movieapp.domain.model.SearchResultItem
import com.example.movieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.example.movieapp.domain.util.Result

class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: TmdbRemoteDataSource
) : MovieRepository {

    override fun getPopularMoviesStream(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { MoviePagingSource(remoteDataSource) }
        ).flow
    }

    override fun searchMedia(query: String, mediaType: MediaType): Flow<PagingData<SearchResultItem>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                object : PagingSource<Int, SearchResultItem>() {
                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResultItem> {
                        return LoadResult.Error(NotImplementedError("SearchPagingSource not implemented"))
                    }
                    override fun getRefreshKey(state: PagingState<Int, SearchResultItem>): Int? = null
                }
            }
        ).flow
    }

    override fun getMovieDetails(id: Int): Flow<Result<MovieDetails>> {
        return flow {
            when (val result = remoteDataSource.getMovieDetails(id)) {
                is Result.Success -> {
                    val domainDetails  = result.data.toDomain()
                    if (domainDetails != null) {
                        emit(Result.Success(domainDetails))
                    } else {
                        emit(Result.Error(Exception("Failed to map movie details DTO")))
                    }
                }
                is Result.Error -> {
                    emit(Result.Error(result.exception))
                }
            }
        }
    }
}