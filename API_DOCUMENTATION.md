# Movie Search API Documentation

This document provides comprehensive documentation for the Movie Search REST API endpoints.

## Base URL

```
http://localhost:8080
```

## Authentication

No authentication is required for these endpoints.

## Content Type

All API responses return JSON with `Content-Type: application/json`.

## Endpoints

### 1. Search Movies

Search and filter movies based on various criteria.

**Endpoint:** `GET /movies/search`

**Description:** Returns a list of movies matching the specified search criteria. All parameters are optional and can be combined for more specific searches.

#### Parameters

| Parameter | Type   | Required | Description                                    | Example Values        |
|-----------|--------|----------|------------------------------------------------|-----------------------|
| `name`    | string | No       | Search movies by name (partial, case-insensitive) | "prison", "the", "action" |
| `id`      | number | No       | Find movie by exact ID (must be positive)     | 1, 5, 12             |
| `genre`   | string | No       | Filter by genre (partial, case-insensitive)   | "drama", "sci", "action" |

#### Request Examples

```bash
# Get all movies
GET /movies/search

# Search by movie name
GET /movies/search?name=prison

# Search by movie ID
GET /movies/search?id=1

# Search by genre
GET /movies/search?genre=drama

# Combined search
GET /movies/search?name=the&genre=drama

# URL encoded search with spaces
GET /movies/search?name=the%20prison
```

#### Response Format

**Success Response (200 OK):**

```json
[
  {
    "id": 1,
    "movieName": "The Prison Escape",
    "director": "John Director",
    "year": 1994,
    "genre": "Drama",
    "description": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
    "duration": 142,
    "imdbRating": 5.0,
    "icon": "🎬"
  },
  {
    "id": 2,
    "movieName": "The Family Boss",
    "director": "Michael Filmmaker",
    "year": 1972,
    "genre": "Crime/Drama",
    "description": "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
    "duration": 175,
    "imdbRating": 5.0,
    "icon": "🎬"
  }
]
```

**Empty Results (200 OK):**

```json
[]
```

**Error Response (400 Bad Request):**

```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/movies/search"
}
```

**Error Response (500 Internal Server Error):**

```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/movies/search"
}
```

#### HTTP Status Codes

| Status Code | Description                                    |
|-------------|------------------------------------------------|
| 200         | Success - Returns matching movies (may be empty) |
| 400         | Bad Request - Invalid parameters (e.g., negative ID) |
| 500         | Internal Server Error - Unexpected server error |

#### Field Descriptions

| Field        | Type   | Description                                    |
|--------------|--------|------------------------------------------------|
| `id`         | number | Unique movie identifier (1-12)                |
| `movieName`  | string | Full title of the movie                       |
| `director`   | string | Director's name                               |
| `year`       | number | Release year                                  |
| `genre`      | string | Movie genre(s), may contain multiple genres   |
| `description`| string | Plot summary or movie description             |
| `duration`   | number | Movie length in minutes                       |
| `imdbRating` | number | Rating from 1.0 to 5.0                      |
| `icon`       | string | Emoji icon representing the movie             |

## Search Behavior

### Name Search
- **Case Insensitive:** "PRISON" matches "The Prison Escape"
- **Partial Matching:** "prison" matches "The Prison Escape"
- **Whitespace Trimmed:** "  prison  " is treated as "prison"

### ID Search
- **Exact Matching:** Only returns the movie with the specified ID
- **Validation:** Negative or zero IDs return 400 Bad Request
- **Not Found:** Non-existent IDs return empty array (not an error)

### Genre Search
- **Case Insensitive:** "DRAMA" matches "Drama" and "Crime/Drama"
- **Partial Matching:** "sci" matches "Action/Sci-Fi"
- **Multi-Genre Support:** Searches within compound genres like "Crime/Drama"

### Combined Search
- **AND Logic:** All specified criteria must match
- **Example:** `name=the&genre=drama` returns movies containing "the" in the name AND "drama" in the genre

## Available Genres

The following genres are available in the movie database:

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

## Code Examples

### JavaScript (Fetch API)

```javascript
// Search for movies with "the" in the name
fetch('/movies/search?name=the')
  .then(response => response.json())
  .then(movies => {
    console.log('Found movies:', movies);
  })
  .catch(error => {
    console.error('Search failed:', error);
  });

// Combined search
const searchParams = new URLSearchParams({
  name: 'the',
  genre: 'drama'
});

fetch(`/movies/search?${searchParams}`)
  .then(response => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
  })
  .then(movies => {
    console.log('Search results:', movies);
  });
```

### Python (requests)

```python
import requests

# Base URL
base_url = "http://localhost:8080"

# Search by name
response = requests.get(f"{base_url}/movies/search", params={"name": "prison"})
if response.status_code == 200:
    movies = response.json()
    print(f"Found {len(movies)} movies")
else:
    print(f"Error: {response.status_code}")

# Combined search
params = {
    "name": "the",
    "genre": "drama"
}
response = requests.get(f"{base_url}/movies/search", params=params)
movies = response.json()
```

### Java (Spring RestTemplate)

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

RestTemplate restTemplate = new RestTemplate();
String baseUrl = "http://localhost:8080";

// Search by genre
String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/movies/search")
    .queryParam("genre", "drama")
    .toUriString();

Movie[] movies = restTemplate.getForObject(url, Movie[].class);
System.out.println("Found " + movies.length + " movies");
```

### cURL Examples

```bash
# Basic search
curl -X GET "http://localhost:8080/movies/search?name=prison"

# Search with multiple parameters
curl -X GET "http://localhost:8080/movies/search?name=the&genre=drama"

# Search by ID
curl -X GET "http://localhost:8080/movies/search?id=1"

# Get all movies
curl -X GET "http://localhost:8080/movies/search"

# With verbose output to see headers
curl -v -X GET "http://localhost:8080/movies/search?genre=action"
```

## Error Handling

### Client-Side Error Handling

Always check the HTTP status code before processing the response:

```javascript
fetch('/movies/search?id=-1')
  .then(response => {
    if (response.status === 400) {
      throw new Error('Invalid search parameters');
    }
    if (response.status === 500) {
      throw new Error('Server error occurred');
    }
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
  })
  .then(movies => {
    if (movies.length === 0) {
      console.log('No movies found matching criteria');
    } else {
      console.log('Found movies:', movies);
    }
  })
  .catch(error => {
    console.error('Search failed:', error.message);
  });
```

### Common Error Scenarios

1. **Invalid ID (400 Bad Request):**
   - Negative ID: `id=-1`
   - Zero ID: `id=0`

2. **No Results (200 OK with empty array):**
   - Non-existent movie name
   - Non-matching genre
   - Overly restrictive combined search

3. **Server Error (500 Internal Server Error):**
   - Database connection issues
   - Unexpected application errors

## Rate Limiting

Currently, there are no rate limits imposed on the API endpoints. However, for production use, consider implementing appropriate rate limiting.

## Versioning

This API does not currently use versioning. Future versions may include version numbers in the URL path (e.g., `/v1/movies/search`).

## Support

For issues or questions about the API:
1. Check the application logs for error details
2. Verify that the Spring Boot application is running
3. Ensure the movies.json data file is properly loaded
4. Test with simple queries before using complex combinations

## Changelog

### Version 1.0.0
- Initial release of movie search API
- Support for name, ID, and genre filtering
- Case-insensitive partial matching
- Combined search criteria support
- Comprehensive error handling