package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "id", required = false) Long id,
                           @RequestParam(value = "genre", required = false) String genre) {
        logger.info("Fetching movies with search criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> movies;
        boolean isSearch = (name != null && !name.trim().isEmpty()) || 
                          (id != null && id > 0) || 
                          (genre != null && !genre.trim().isEmpty());
        
        if (isSearch) {
            movies = movieService.searchMovies(name, id, genre);
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            model.addAttribute("resultCount", movies.size());
        } else {
            movies = movieService.getAllMovies();
            model.addAttribute("searchPerformed", false);
        }
        
        model.addAttribute("movies", movies);
        model.addAttribute("allGenres", movieService.getAllGenres());
        return "movies";
    }

    /**
     * REST API endpoint for movie search.
     * Returns JSON response for API consumers.
     * 
     * @param name Movie name to search for (optional, partial matching)
     * @param id Exact movie ID to search for (optional)
     * @param genre Genre to search for (optional, partial matching)
     * @return ResponseEntity with list of matching movies
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<List<Movie>> searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("API search request - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            // Validate ID parameter if provided
            if (id != null && id <= 0) {
                logger.warn("Invalid movie ID provided: {}", id);
                return ResponseEntity.badRequest().build();
            }
            
            List<Movie> results = movieService.searchMovies(name, id, genre);
            logger.info("API search returned {} results", results.size());
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            logger.error("Error during movie search: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}