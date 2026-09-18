package com.example.movieapp.domain

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
import kotlin.test.assertNull

class SharedDomainTest {

    @Test
    fun movieHoldsProvidedFieldsIncludingNullablePoster() {
        val movie = Movie(
            id = 42,
            title = "Dune",
            overview = "A desert planet",
            posterUrl = null,
            releaseYear = "2021",
        )

        assertEquals(42, movie.id)
        assertEquals("Dune", movie.title)
        assertEquals("A desert planet", movie.overview)
        assertNull(movie.posterUrl)
        assertEquals("2021", movie.releaseYear)
    }

    @Test
    fun resultSuccessAndErrorPreservePayload() {
        val details = sampleDetails()
        val success = Result.Success(details)
        val error = Result.Error(IllegalStateException("network down"))

        assertEquals(details, success.data)
        assertEquals("network down", error.exception.message)
    }

    @Test
    fun fakeRepositoryEmitsRequestedMovieDetails() = runTest {
        val details = sampleDetails()
        val repository: MovieRepository = FakeMovieRepository(details)

        val result = repository.getMovieDetails(details.id).first()

        assertIs<Result.Success<MovieDetails>>(result)
        assertEquals(details, result.data)
    }

    @Test
    fun searchResultItemDefaultsToMovieMediaTypeInThisApp() {
        val item = SearchResultItem(
            id = 7,
            title = "Arrival",
            overview = "Language",
            posterUrl = "/arrival.jpg",
            mediaType = MediaType.MOVIE,
        )
        assertEquals(MediaType.MOVIE, item.mediaType)
    }

    private fun sampleDetails() = MovieDetails(
        id = 101,
        title = "Heat",
        overview = "A cop and a thief",
        posterUrl = "/heat.jpg",
        releaseYear = "1995",
        rating = 8.3,
        genres = listOf("Crime", "Drama"),
        runtimeMinutes = 170,
    )
}

private class FakeMovieRepository(
    private val details: MovieDetails,
) : MovieRepository {
    override fun getPopularMoviesStream(): Flow<PagingData<Movie>> =
        flowOf(PagingData.empty())

    override fun searchMedia(
        query: String,
        mediaType: MediaType,
    ): Flow<PagingData<SearchResultItem>> = flowOf(PagingData.empty())

    override fun getMovieDetails(id: Int): Flow<Result<MovieDetails>> =
        flowOf(Result.Success(details))
}
