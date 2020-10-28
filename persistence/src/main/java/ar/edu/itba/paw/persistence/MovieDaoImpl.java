package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Repository
public class MovieDaoImpl implements MovieDao {

    @Override
    public Optional<Movie> findMovieById(long movie_id) {
        return Optional.empty();
    }

    @Override
    public Movie register(String title, String originalTitle, long tmdbId, String imdbId, String originalLanguage, String overview, float popularity, float runtime, float voteAverage, LocalDate releaseDate, Collection<Long> genres) {
        return null;
    }

    @Override
    public Collection<Movie> findMoviesByPost(Post post) {
        return null;
    }

    @Override
    public PaginatedCollection<Movie> getAllMovies(SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public Collection<Movie> getAllMoviesNotPaginated() {
        return null;
    }

    @Override
    public PaginatedCollection<Movie> searchMovies(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Movie> searchMoviesByCategory(String query, String category, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Movie> searchMoviesByReleaseDate(String query, LocalDate since, LocalDate upTo, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Movie> searchMoviesByCategoryAndReleaseDate(String query, String category, LocalDate since, LocalDate upTo, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }
}