package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.MovieCategory;


import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface MovieService {

    Optional<Movie> findById(long id);

    Movie register(String title, String originalTitle, long tmdbId, String imdbId, String originalLanguage,
                   String overview, float popularity, float runtime, float voteAverage, LocalDate releaseDate, Collection<Long> categories);

    Collection<Movie> getAllMovies();

    Collection<Movie> findMoviesByPostId(long postId);

    Collection<MovieCategory> getAvailableCategories();
}
