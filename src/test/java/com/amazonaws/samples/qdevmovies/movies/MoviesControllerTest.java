package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                    new Movie(3L, "The Great Film", "Great Director", 2021, "Drama", "Great description", 130, 4.8)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>(allMovies);
                
                if (id != null && id > 0) {
                    results = results.stream()
                            .filter(movie -> movie.getId() == id)
                            .collect(ArrayList::new, (list, movie) -> list.add(movie), ArrayList::addAll);
                }
                
                if (name != null && !name.trim().isEmpty()) {
                    String searchName = name.trim().toLowerCase();
                    results = results.stream()
                            .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                            .collect(ArrayList::new, (list, movie) -> list.add(movie), ArrayList::addAll);
                }
                
                if (genre != null && !genre.trim().isEmpty()) {
                    String searchGenre = genre.trim().toLowerCase();
                    results = results.stream()
                            .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                            .collect(ArrayList::new, (list, movie) -> list.add(movie), ArrayList::addAll);
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies template when no search criteria provided")
    public void testGetMovies_NoSearchCriteria() {
        String result = moviesController.getMovies(model, null, null, null);
        
        assertNotNull(result);
        assertEquals("movies", result);
        assertEquals(false, model.getAttribute("searchPerformed"));
        assertNotNull(model.getAttribute("movies"));
        assertNotNull(model.getAttribute("allGenres"));
    }

    @Test
    @DisplayName("Should perform search when search criteria provided")
    public void testGetMovies_WithSearchCriteria() {
        String result = moviesController.getMovies(model, "test", null, null);
        
        assertNotNull(result);
        assertEquals("movies", result);
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals("test", model.getAttribute("searchName"));
        assertNotNull(model.getAttribute("resultCount"));
    }

    @Test
    @DisplayName("Should search by movie ID")
    public void testGetMovies_SearchById() {
        String result = moviesController.getMovies(model, null, 1L, null);
        
        assertEquals("movies", result);
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals(1L, model.getAttribute("searchId"));
        assertEquals(1, model.getAttribute("resultCount"));
    }

    @Test
    @DisplayName("Should search by genre")
    public void testGetMovies_SearchByGenre() {
        String result = moviesController.getMovies(model, null, null, "drama");
        
        assertEquals("movies", result);
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals("drama", model.getAttribute("searchGenre"));
        assertNotNull(model.getAttribute("resultCount"));
    }

    @Test
    @DisplayName("Should return movie details for valid ID")
    public void testGetMovieDetails_ValidId() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    @DisplayName("Should return error page for invalid movie ID")
    public void testGetMovieDetails_InvalidId() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    // REST API Tests

    @Test
    @DisplayName("Should return all movies when no search criteria provided via API")
    public void testSearchMoviesAPI_NoSearchCriteria() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies(null, null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
    }

    @Test
    @DisplayName("Should search movies by name via API")
    public void testSearchMoviesAPI_ByName() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies("test", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).getMovieName().toLowerCase().contains("test"));
    }

    @Test
    @DisplayName("Should search movies by ID via API")
    public void testSearchMoviesAPI_ById() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies(null, 1L, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
    }

    @Test
    @DisplayName("Should search movies by genre via API")
    public void testSearchMoviesAPI_ByGenre() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies(null, null, "drama");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        for (Movie movie : response.getBody()) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should return bad request for invalid ID via API")
    public void testSearchMoviesAPI_InvalidId() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies(null, -1L, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return empty results for non-matching search via API")
    public void testSearchMoviesAPI_NoMatches() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies("nonexistent", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("Should combine multiple search criteria via API")
    public void testSearchMoviesAPI_MultipleCriteria() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies("the", null, "drama");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        for (Movie movie : response.getBody()) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should handle whitespace in search parameters via API")
    public void testSearchMoviesAPI_WithWhitespace() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies("  test  ", null, "  drama  ");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should return zero ID as invalid via API")
    public void testSearchMoviesAPI_ZeroId() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies(null, 0L, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Integration Tests

    @Test
    @DisplayName("Should integrate movie service correctly")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should integrate search functionality correctly")
    public void testSearchIntegration() {
        List<Movie> results = mockMovieService.searchMovies("test", null, null);
        assertEquals(1, results.size());
        assertEquals("Test Movie", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should integrate genre functionality correctly")
    public void testGenreIntegration() {
        List<String> genres = mockMovieService.getAllGenres();
        assertEquals(2, genres.size());
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Action"));
    }
}
