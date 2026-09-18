package com.example.movieapp.domain.usecase

import androidx.paging.PagingData
import com.example.movieapp.domain.model.MediaType
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.MovieDetails
import com.example.movieapp.domain.model.SearchResultItem
import com.example.movieapp.domain.repository.MovieRepository
import com.example.movieapp.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class UseCaseTest {

    @Test
    fun getPopularMoviesUseCaseReturnsRepositoryStream() {
        val popular = flowOf(PagingData.empty<Movie>())
        val repository = RecordingMovieRepository(popular = popular)

        val result = GetPopularMoviesUseCase(repository)()

        assertSame(popular, result)
    }

    @Test
    fun searchMediaUseCaseTrimsQueryBeforeCallingRepository() {
        val search = flowOf(PagingData.empty<SearchResultItem>())
        val repository = RecordingMovieRepository(search = search)

        val result = SearchMediaUseCase(repository)("  Dune  ", MediaType.MOVIE)

        assertEquals("Dune", repository.lastSearchQuery)
        assertEquals(MediaType.MOVIE, repository.lastSearchMediaType)
        assertSame(search, result)
    }

    @Test
    fun getMovieDetailsUseCaseDelegatesToRepository() = runTest {
        val details = MovieDetails(
            id = 550,
            title = "Fight Club",
            overview = "An insomniac office worker",
            posterUrl = "/poster.jpg",
            releaseYear = "1999",
            rating = 8.4,
            genres = listOf("Drama"),
            runtimeMinutes = 139,
        )
        val detailsFlow = flowOf(Result.Success(details))
        val repository = RecordingMovieRepository(details = detailsFlow)

        val result = GetMovieDetailsUseCase(repository)(550).first()

        assertEquals(550, repository.lastDetailsId)
        val success = assertIs<Result.Success<MovieDetails>>(result)
        assertEquals(details, success.data)
    }
}

private class RecordingMovieRepository(
    private val popular: Flow<PagingData<Movie>> = flowOf(PagingData.empty()),
    private val search: Flow<PagingData<SearchResultItem>> = flowOf(PagingData.empty()),
    private val details: Flow<Result<MovieDetails>> = flowOf(
        Result.Error(IllegalStateException("not stubbed")),
    ),
) : MovieRepository {
    var lastSearchQuery: String? = null
    var lastSearchMediaType: MediaType? = null
    var lastDetailsId: Int? = null

    override fun getPopularMoviesStream(): Flow<PagingData<Movie>> = popular

    override fun searchMedia(
        query: String,
        mediaType: MediaType,
    ): Flow<PagingData<SearchResultItem>> {
        lastSearchQuery = query
        lastSearchMediaType = mediaType
        return search
    }

    override fun getMovieDetails(id: Int): Flow<Result<MovieDetails>> {
        lastDetailsId = id
        return details
    }
}
