package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Movie;


import java.time.LocalDate;
import java.util.Optional;

public interface MovieService {

    public Optional<Movie> findById(long id);

    public Movie register(String title, LocalDate premierDate);
}
