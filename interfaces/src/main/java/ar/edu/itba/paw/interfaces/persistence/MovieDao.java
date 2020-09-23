package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Movie;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface MovieDao {

    Optional<Movie> findById(long id);

    Movie register(String title, String originalTitle, long tmdbId, String imdbId, String originalLanguage, String overview, float popularity, float runtime, float voteAverage, LocalDate releaseDate, Collection<Long> genres);

    Collection<Movie> findMoviesByPostId(long postId);

    Collection<Movie> getAllMovies();

    Collection<Movie> searchMovies(String query);
}
