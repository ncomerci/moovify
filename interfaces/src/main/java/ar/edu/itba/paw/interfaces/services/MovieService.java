package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Movie;


import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface MovieService {

    Optional<Movie> findById(long id);

    Movie register(String title, LocalDate premierDate);

    Collection<Movie> getAllMovies();

    Collection<Movie> findMoviesByPostId(long postId);
}
