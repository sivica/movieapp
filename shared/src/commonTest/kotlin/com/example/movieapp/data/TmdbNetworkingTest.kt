package com.example.movieapp.data

import androidx.paging.PagingSource
import com.example.movieapp.data.datasource.TmdbRemoteDataSource
import com.example.movieapp.data.paging.MoviePagingSource
import com.example.movieapp.data.paging.SearchPagingSource
import com.example.movieapp.data.remote.TmdbApiException
import com.example.movieapp.data.remote.createTmdbHttpClient
import com.example.movieapp.data.repository.MovieRepositoryImpl
import com.example.movieapp.domain.model.MediaType
import com.example.movieapp.domain.util.Result
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TmdbNetworkingTest {

    @Test
    fun popularMoviesRequestIncludesApiKeyAndMapsDomainMovies() = runTest {
        val engine = MockEngine { request ->
            assertEquals("test-key", request.url.parameters["api_key"])
            assertEquals("1", request.url.parameters["page"])
            assertTrue(request.url.encodedPath.endsWith("/movie/popular"))
            jsonResponse(POPULAR_JSON)
        }
        val dataSource = dataSource(engine)

        val result = dataSource.getPopularMovies(page = 1)

        val page = assertIs<Result.Success<*>>(result).data as com.example.movieapp.data.remote.MovieListDTO
        assertEquals(1, page.page)
        assertEquals("Fight Club", page.results.single().title)
    }

    @Test
    fun movieDetailsSuccessIsMappedByRepository() = runTest {
        val engine = MockEngine { request ->
            assertEquals("test-key", request.url.parameters["api_key"])
            assertTrue(request.url.encodedPath.endsWith("/movie/550"))
            jsonResponse(DETAILS_JSON)
        }
        val repository = MovieRepositoryImpl(dataSource(engine))

        val result = repository.getMovieDetails(550).first()

        val details = assertIs<Result.Success<com.example.movieapp.domain.model.MovieDetails>>(result).data
        assertEquals(550, details.id)
        assertEquals("Fight Club", details.title)
        assertEquals(listOf("Drama"), details.genres)
        assertEquals(139, details.runtimeMinutes)
        assertEquals("https://image.tmdb.org/t/p/w500//pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg", details.posterUrl)
    }

    @Test
    fun httpErrorBecomesResultError() = runTest {
        val engine = MockEngine {
            jsonResponse("{\"status_message\":\"offline\"}", HttpStatusCode.InternalServerError)
        }

        val result = dataSource(engine).getPopularMovies(page = 1)

        val error = assertIs<Result.Error>(result)
        assertIs<TmdbApiException>(error.exception)
        assertEquals("HTTP 500", error.exception.message)
    }

    @Test
    fun unmappableDetailsBecomeRepositoryError() = runTest {
        val engine = MockEngine {
            jsonResponse("""{"overview":"missing identity"}""")
        }
        val repository = MovieRepositoryImpl(dataSource(engine))

        val result = repository.getMovieDetails(1).first()

        val error = assertIs<Result.Error>(result)
        assertEquals("Failed to map movie details DTO", error.exception.message)
    }

    @Test
    fun moviePagingSourceLoadsMappedPage() = runTest {
        val engine = MockEngine { jsonResponse(POPULAR_JSON) }
        val pagingSource = MoviePagingSource(dataSource(engine))

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        val page = assertIs<PagingSource.LoadResult.Page<Int, com.example.movieapp.domain.model.Movie>>(loadResult)
        assertEquals("Fight Club", page.data.single().title)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun searchPagingSourceUsesQueryAndBlankShortCircuits() = runTest {
        val blank = SearchPagingSource(dataSource(MockEngine { error("should not call network") }), query = "  ")
        val blankPage = assertIs<PagingSource.LoadResult.Page<Int, com.example.movieapp.domain.model.SearchResultItem>>(
            blank.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false))
        )
        assertEquals(emptyList(), blankPage.data)

        val engine = MockEngine { request ->
            assertEquals("club", request.url.parameters["query"])
            assertEquals("1", request.url.parameters["page"])
            assertTrue(request.url.encodedPath.endsWith("/search/movie"))
            jsonResponse(POPULAR_JSON)
        }
        val search = SearchPagingSource(dataSource(engine), query = "club")
        val loaded = assertIs<PagingSource.LoadResult.Page<Int, com.example.movieapp.domain.model.SearchResultItem>>(
            search.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false))
        )
        assertEquals("Fight Club", loaded.data.single().title)
        assertEquals(MediaType.MOVIE, loaded.data.single().mediaType)
    }

    private fun dataSource(engine: MockEngine) =
        TmdbRemoteDataSource(createTmdbHttpClient(apiKey = "test-key", engine = engine))

    private fun jsonResponse(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ) = respond(
        content = body,
        status = status,
        headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )

    private companion object {
        const val POPULAR_JSON = """
            {
              "page": 1,
              "results": [
                {
                  "id": 550,
                  "title": "Fight Club",
                  "overview": "An insomniac office worker",
                  "poster_path": "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
                  "release_date": "1999-10-15"
                }
              ],
              "total_pages": 2,
              "total_results": 40
            }
        """

        const val DETAILS_JSON = """
            {
              "id": 550,
              "title": "Fight Club",
              "overview": "An insomniac office worker",
              "poster_path": "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
              "release_date": "1999-10-15",
              "vote_average": 8.4,
              "runtime": 139,
              "genres": [{"id": 18, "name": "Drama"}]
            }
        """
    }
}
