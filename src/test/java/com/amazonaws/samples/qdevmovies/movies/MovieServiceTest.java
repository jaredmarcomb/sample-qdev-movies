package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MovieService search functionality.
 * Tests search methods, filtering logic, and edge cases.
 */
class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should return all movies when no search criteria provided")
    void testSearchMovies_NoSearchCriteria_ReturnsAllMovies() {
        // When
        List<Movie> results = movieService.searchMovies(null, null, null);
        List<Movie> allMovies = movieService.getAllMovies();

        // Then
        assertEquals(allMovies.size(), results.size());
        assertEquals(allMovies, results);
    }

    @Test
    @DisplayName("Should return all movies when empty search criteria provided")
    void testSearchMovies_EmptySearchCriteria_ReturnsAllMovies() {
        // When
        List<Movie> results = movieService.searchMovies("", null, "");
        List<Movie> allMovies = movieService.getAllMovies();

        // Then
        assertEquals(allMovies.size(), results.size());
        assertEquals(allMovies, results);
    }

    @Test
    @DisplayName("Should filter movies by exact ID")
    void testSearchMovies_ByExactId_ReturnsMatchingMovie() {
        // Given
        Long searchId = 1L;

        // When
        List<Movie> results = movieService.searchMovies(null, searchId, null);

        // Then
        assertEquals(1, results.size());
        assertEquals(searchId, results.get(0).getId());
    }

    @Test
    @DisplayName("Should return empty list for non-existent ID")
    void testSearchMovies_ByNonExistentId_ReturnsEmptyList() {
        // Given
        Long nonExistentId = 999L;

        // When
        List<Movie> results = movieService.searchMovies(null, nonExistentId, null);

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should filter movies by partial name match (case insensitive)")
    void testSearchMovies_ByPartialName_ReturnsMatchingMovies() {
        // Given
        String searchName = "the"; // Should match "The Prison Escape", "The Family Boss", etc.

        // When
        List<Movie> results = movieService.searchMovies(searchName, null, null);

        // Then
        assertFalse(results.isEmpty());
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains(searchName.toLowerCase()));
        }
    }

    @Test
    @DisplayName("Should filter movies by exact name match (case insensitive)")
    void testSearchMovies_ByExactName_ReturnsMatchingMovie() {
        // Given
        String exactName = "the prison escape";

        // When
        List<Movie> results = movieService.searchMovies(exactName, null, null);

        // Then
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should filter movies by genre (case insensitive)")
    void testSearchMovies_ByGenre_ReturnsMatchingMovies() {
        // Given
        String searchGenre = "drama";

        // When
        List<Movie> results = movieService.searchMovies(null, null, searchGenre);

        // Then
        assertFalse(results.isEmpty());
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains(searchGenre.toLowerCase()));
        }
    }

    @Test
    @DisplayName("Should filter movies by partial genre match")
    void testSearchMovies_ByPartialGenre_ReturnsMatchingMovies() {
        // Given
        String partialGenre = "sci"; // Should match "Action/Sci-Fi"

        // When
        List<Movie> results = movieService.searchMovies(null, null, partialGenre);

        // Then
        assertFalse(results.isEmpty());
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains(partialGenre.toLowerCase()));
        }
    }

    @Test
    @DisplayName("Should combine multiple search criteria with AND logic")
    void testSearchMovies_MultipleCriteria_ReturnsMatchingMovies() {
        // Given
        String searchName = "the";
        String searchGenre = "drama";

        // When
        List<Movie> results = movieService.searchMovies(searchName, null, searchGenre);

        // Then
        assertFalse(results.isEmpty());
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains(searchName.toLowerCase()));
            assertTrue(movie.getGenre().toLowerCase().contains(searchGenre.toLowerCase()));
        }
    }

    @Test
    @DisplayName("Should return empty list when no movies match criteria")
    void testSearchMovies_NoMatches_ReturnsEmptyList() {
        // Given
        String nonExistentName = "NonExistentMovie";

        // When
        List<Movie> results = movieService.searchMovies(nonExistentName, null, null);

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle whitespace in search terms")
    void testSearchMovies_WithWhitespace_TrimsAndSearches() {
        // Given
        String nameWithWhitespace = "  the  ";
        String genreWithWhitespace = "  drama  ";

        // When
        List<Movie> results = movieService.searchMovies(nameWithWhitespace, null, genreWithWhitespace);

        // Then
        assertFalse(results.isEmpty());
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should ignore invalid ID values")
    void testSearchMovies_InvalidId_IgnoresIdFilter() {
        // Given
        Long invalidId = -1L;
        String searchName = "the";

        // When
        List<Movie> results = movieService.searchMovies(searchName, invalidId, null);

        // Then
        // Should return results based on name only, ignoring invalid ID
        assertFalse(results.isEmpty());
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    @DisplayName("Should return all unique genres")
    void testGetAllGenres_ReturnsUniqueGenres() {
        // When
        List<String> genres = movieService.getAllGenres();

        // Then
        assertFalse(genres.isEmpty());
        // Check that genres are unique
        assertEquals(genres.size(), genres.stream().distinct().count());
        // Check that genres are sorted
        List<String> sortedGenres = genres.stream().sorted().toList();
        assertEquals(sortedGenres, genres);
    }

    @Test
    @DisplayName("Should return movie by valid ID")
    void testGetMovieById_ValidId_ReturnsMovie() {
        // Given
        Long validId = 1L;

        // When
        Optional<Movie> result = movieService.getMovieById(validId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(validId, result.get().getId());
    }

    @Test
    @DisplayName("Should return empty optional for invalid ID")
    void testGetMovieById_InvalidId_ReturnsEmpty() {
        // Given
        Long invalidId = -1L;

        // When
        Optional<Movie> result = movieService.getMovieById(invalidId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return empty optional for null ID")
    void testGetMovieById_NullId_ReturnsEmpty() {
        // When
        Optional<Movie> result = movieService.getMovieById(null);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return all movies from getAllMovies")
    void testGetAllMovies_ReturnsAllMovies() {
        // When
        List<Movie> movies = movieService.getAllMovies();

        // Then
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // Based on the movies.json file, we should have 12 movies
        assertEquals(12, movies.size());
    }

    @Test
    @DisplayName("Should perform case-insensitive search for movie names")
    void testSearchMovies_CaseInsensitiveName() {
        // Given
        String upperCaseName = "THE PRISON";
        String lowerCaseName = "the prison";
        String mixedCaseName = "ThE pRiSoN";

        // When
        List<Movie> upperResults = movieService.searchMovies(upperCaseName, null, null);
        List<Movie> lowerResults = movieService.searchMovies(lowerCaseName, null, null);
        List<Movie> mixedResults = movieService.searchMovies(mixedCaseName, null, null);

        // Then
        assertEquals(upperResults.size(), lowerResults.size());
        assertEquals(lowerResults.size(), mixedResults.size());
        assertEquals(upperResults, lowerResults);
        assertEquals(lowerResults, mixedResults);
    }

    @Test
    @DisplayName("Should perform case-insensitive search for genres")
    void testSearchMovies_CaseInsensitiveGenre() {
        // Given
        String upperCaseGenre = "DRAMA";
        String lowerCaseGenre = "drama";
        String mixedCaseGenre = "DrAmA";

        // When
        List<Movie> upperResults = movieService.searchMovies(null, null, upperCaseGenre);
        List<Movie> lowerResults = movieService.searchMovies(null, null, lowerCaseGenre);
        List<Movie> mixedResults = movieService.searchMovies(null, null, mixedCaseGenre);

        // Then
        assertEquals(upperResults.size(), lowerResults.size());
        assertEquals(lowerResults.size(), mixedResults.size());
        assertEquals(upperResults, lowerResults);
        assertEquals(lowerResults, mixedResults);
    }
}