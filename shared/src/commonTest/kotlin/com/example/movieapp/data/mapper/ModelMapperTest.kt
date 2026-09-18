package com.example.movieapp.data.mapper

import com.example.movieapp.data.remote.GenreDTO
import com.example.movieapp.data.remote.MovieDTO
import com.example.movieapp.data.remote.MovieDetailsDTO
import com.example.movieapp.data.remote.TmdbConfig
import com.example.movieapp.domain.model.MediaType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ModelMapperTest {

    @Test
    fun movieDtoMapsPosterUrlAndReleaseYear() {
        val movie = sampleMovieDto().toDomain()

        assertEquals(550, movie.id)
        assertEquals("Fight Club", movie.title)
        assertEquals("An insomniac office worker", movie.overview)
        assertEquals("${TmdbConfig.BASE_POSTER_IMAGE_URL}/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg", movie.posterUrl)
        assertEquals("1999", movie.releaseYear)
    }

    @Test
    fun movieDtoKeepsNullPosterAndIgnoresShortReleaseDate() {
        val movie = sampleMovieDto(posterPath = null, releaseDate = "199").toDomain()

        assertNull(movie.posterUrl)
        assertNull(movie.releaseYear)
    }

    @Test
    fun detailsDtoMapsGenresAndDefaults() {
        val details = sampleDetailsDto().toDomain()

        assertEquals(550, details?.id)
        assertEquals("Fight Club", details?.title)
        assertEquals(8.4, details?.rating)
        assertEquals(listOf("Drama"), details?.genres)
        assertEquals(139, details?.runtimeMinutes)
        assertEquals("1999", details?.releaseYear)
    }

    @Test
    fun detailsDtoReturnsNullWhenIdOrTitleMissing() {
        assertNull(sampleDetailsDto(id = null).toDomain())
        assertNull(sampleDetailsDto(title = null).toDomain())
    }

    @Test
    fun detailsDtoDropsBlankGenreNamesAndEmptyOverview() {
        val details = sampleDetailsDto(
            overview = null,
            voteAverage = null,
            genres = listOf(GenreDTO(id = 1, name = "Drama"), GenreDTO(id = 2, name = null)),
            posterPath = null,
            releaseDate = null,
            runtime = null,
        ).toDomain()

        assertEquals("", details?.overview)
        assertEquals(0.0, details?.rating)
        assertEquals(listOf("Drama"), details?.genres)
        assertNull(details?.posterUrl)
        assertNull(details?.releaseYear)
        assertNull(details?.runtimeMinutes)
    }

    @Test
    fun searchResultUsesMovieMediaTypeAndPosterUrl() {
        val item = sampleMovieDto().toSearchResultItemDomain()

        assertEquals(550, item.id)
        assertEquals(MediaType.MOVIE, item.mediaType)
        assertEquals("${TmdbConfig.BASE_POSTER_IMAGE_URL}/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg", item.posterUrl)
    }

    private fun sampleMovieDto(
        posterPath: String? = "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
        releaseDate: String? = "1999-10-15",
    ) = MovieDTO(
        id = 550,
        overview = "An insomniac office worker",
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = "Fight Club",
    )

    private fun sampleDetailsDto(
        id: Int? = 550,
        title: String? = "Fight Club",
        overview: String? = "An insomniac office worker",
        posterPath: String? = "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
        releaseDate: String? = "1999-10-15",
        voteAverage: Double? = 8.4,
        genres: List<GenreDTO>? = listOf(GenreDTO(id = 18, name = "Drama")),
        runtime: Int? = 139,
    ) = MovieDetailsDTO(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genres = genres,
        runtime = runtime,
    )
}
