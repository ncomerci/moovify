package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Movie;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface MovieDao {

    Optional<Movie> findById(long id);

    Movie register(String title, LocalDate premierDate);

    Set<Movie> getMoviesByPost(long postId);

    Set<Movie> getAllMovies();
}
