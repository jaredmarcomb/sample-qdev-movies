# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with advanced search and filtering capabilities.

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Advanced Search**: Search movies by name, ID, or genre with real-time filtering
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **REST API**: Full REST API support for programmatic access to movie data
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Clean interface with intuitive search forms and result displays

## Technology Stack

- **Java 8**
- **Spring Boot 2.7.18**
- **Thymeleaf** for server-side templating
- **Maven** for dependency management
- **Log4j 2** for logging
- **JUnit 5.8.2** for testing

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List with Search**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Search API**: http://localhost:8080/movies/search

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic for movie operations
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Movie list with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Unit and integration tests
```

## API Endpoints

### Web Interface Endpoints

#### Get All Movies (with Search)
```
GET /movies
```
Returns an HTML page displaying movies with an integrated search form.

**Query Parameters (all optional):**
- `name` (string): Search movies by name (partial matching, case-insensitive)
- `id` (number): Find movie by exact ID
- `genre` (string): Filter movies by genre (partial matching, case-insensitive)

**Examples:**
```
http://localhost:8080/movies                           # All movies
http://localhost:8080/movies?name=prison               # Movies with "prison" in name
http://localhost:8080/movies?id=1                      # Movie with ID 1
http://localhost:8080/movies?genre=drama               # Movies with "drama" in genre
http://localhost:8080/movies?name=the&genre=drama      # Combined search
```

#### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### REST API Endpoints

#### Search Movies API
```
GET /movies/search
```
Returns JSON array of movies matching search criteria.

**Query Parameters (all optional):**
- `name` (string): Search movies by name (partial matching, case-insensitive)
- `id` (number): Find movie by exact ID (must be positive)
- `genre` (string): Filter movies by genre (partial matching, case-insensitive)

**Response Format:**
```json
[
  {
    "id": 1,
    "movieName": "The Prison Escape",
    "director": "John Director",
    "year": 1994,
    "genre": "Drama",
    "description": "Two imprisoned men bond over a number of years...",
    "duration": 142,
    "imdbRating": 5.0,
    "icon": "🎬"
  }
]
```

**HTTP Status Codes:**
- `200 OK`: Successful search (may return empty array)
- `400 Bad Request`: Invalid parameters (e.g., negative ID)
- `500 Internal Server Error`: Server error during search

**Examples:**
```bash
# Get all movies
curl "http://localhost:8080/movies/search"

# Search by name
curl "http://localhost:8080/movies/search?name=prison"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Search by genre
curl "http://localhost:8080/movies/search?genre=drama"

# Combined search
curl "http://localhost:8080/movies/search?name=the&genre=drama"

# Search with URL encoding for spaces
curl "http://localhost:8080/movies/search?name=the%20prison"
```

## Search Features

### Web Interface Search
- **Interactive Form**: Easy-to-use search form with labeled input fields
- **Auto-suggestions**: Genre field includes datalist with available genres
- **Real-time Feedback**: Shows search results count and handles empty results
- **Persistent Search**: Search terms remain in form after submission
- **Clear Search**: One-click button to clear all search criteria
- **Responsive Design**: Search form adapts to mobile and desktop screens

### Search Capabilities
- **Name Search**: Partial, case-insensitive matching on movie titles
- **ID Search**: Exact matching by movie ID with validation
- **Genre Search**: Partial, case-insensitive matching on genres
- **Combined Search**: All criteria work together with AND logic
- **Whitespace Handling**: Automatic trimming of search terms
- **Empty Handling**: Graceful handling of empty or null parameters

### Available Genres
The system includes the following genres for filtering:
- Action
- Action/Crime
- Action/Sci-Fi
- Adventure/Fantasy
- Adventure/Sci-Fi
- Crime/Drama
- Drama
- Drama/History
- Drama/Romance
- Drama/Thriller

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Classes
```bash
# Test movie service functionality
mvn test -Dtest=MovieServiceTest

# Test controller endpoints
mvn test -Dtest=MoviesControllerTest
```

### Test Coverage
The application includes comprehensive test coverage for:
- **Unit Tests**: Service layer logic and edge cases
- **Integration Tests**: Controller endpoints and request handling
- **Search Functionality**: All search combinations and error scenarios
- **API Tests**: REST endpoint responses and status codes

## Error Handling

### Web Interface
- **No Results**: User-friendly message with option to view all movies
- **Invalid Search**: Form validation prevents invalid submissions
- **Server Errors**: Graceful error pages with helpful messages

### REST API
- **Invalid Parameters**: Returns 400 Bad Request with appropriate HTTP status
- **Server Errors**: Returns 500 Internal Server Error for unexpected issues
- **Empty Results**: Returns 200 OK with empty array (not an error condition)

## Performance Considerations

- **In-Memory Data**: Movie data is loaded once at startup for fast access
- **Efficient Filtering**: Stream-based filtering for optimal performance
- **Minimal Dependencies**: Lightweight application with fast startup time
- **Caching**: Movie data and genres are cached in memory

## Troubleshooting

### Port 8080 already in use
Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures
Clean and rebuild:
```bash
mvn clean compile
```

### Search not working
1. Check that the application started successfully
2. Verify the movies.json file is present in resources
3. Check application logs for any errors during startup

### API returns empty results
- Verify search parameters are correctly formatted
- Check that movie data matches your search criteria
- Use case-insensitive search terms

## Development

### Adding New Movies
1. Edit `src/main/resources/movies.json`
2. Follow the existing JSON structure
3. Restart the application to load new data

### Extending Search Functionality
The search system is designed to be easily extensible:
- Add new search parameters to `MovieService.searchMovies()`
- Update the controller methods to accept new parameters
- Enhance the HTML form with additional input fields
- Add corresponding unit tests

### Custom Styling
- Modify the embedded CSS in `movies.html`
- Add external CSS files in `src/main/resources/static/css/`
- Update Thymeleaf templates for new UI components

## Contributing

This project demonstrates modern Spring Boot development practices:
- RESTful API design
- Comprehensive error handling
- Extensive unit testing
- Responsive web design
- Clean code architecture

Feel free to:
- Add more movies to the catalog
- Enhance the search functionality
- Improve the UI/UX design
- Add new features like sorting or pagination
- Implement additional API endpoints

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.
