@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.movieapp.ui.movielist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.GetPopularMoviesUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class MovieListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase
    private lateinit var viewModel: MovieListViewModel


    @Before
    fun setup() {
        getPopularMoviesUseCase = mockk()
    }

    @Test
    fun `popularMoviesFlow should expose PagingData stream from use case`() = runTest {
        val testMovies = listOf(
            Movie(
                id = 1,
                title = "Movie One",
                overview = "Overview One",
                posterUrl = "/poster1.jpg",
                releaseYear = "01"
            ),
            Movie(
                id = 2,
                title = "Movie Two",
                overview = "Overview Two",
                posterUrl = "/poster2.jpg",
                releaseYear = "02"
            )
        )

        val testFlow = Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = testMovies.asPagingSourceFactory()
        ).flow

        every { getPopularMoviesUseCase() } returns testFlow

        viewModel = MovieListViewModel(getPopularMoviesUseCase)

        val snapshot: List<Movie> = viewModel.popularMoviesFlow.asSnapshot {
            scrollTo(index = 1)
        }
        assertEquals(testMovies, snapshot)
    }
}
