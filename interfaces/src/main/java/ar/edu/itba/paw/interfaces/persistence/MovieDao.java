package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.PaginatedCollection;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface MovieDao {

    enum SortCriteria {
        NEWEST, OLDEST, TITLE, POST_COUNT
    }

    Optional<Movie> findById(long id);

    Movie register(String title, String originalTitle, long tmdbId, String imdbId, String originalLanguage, String overview, float popularity, float runtime, float voteAverage, LocalDate releaseDate, Collection<Long> genres);

    Collection<Movie> findMoviesByPostId(long postId);

    PaginatedCollection<Movie> getAllMovies(SortCriteria sortCriteria, int pageNumber, int pageSize);

    Collection<Movie> getAllMoviesNotPaginated();

    PaginatedCollection<Movie> searchMovies(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);
}
