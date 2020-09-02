package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Movie;

import java.time.LocalDate;
import java.util.Optional;

public interface MovieDao {

    public Optional<Movie> findById(long id);

    public Movie register(String title, LocalDate premierDate);
}
