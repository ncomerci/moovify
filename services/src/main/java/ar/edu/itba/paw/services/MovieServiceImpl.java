package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.models.Movie;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;

public class MovieServiceImpl implements MovieDao{

    @Autowired
    private MovieDao movieDao;


    @Override
    public Optional<Movie> findById(long id) {
        return movieDao.findById(id);
    }

    @Override
    public Movie register(String title, LocalDate premierDate) {
        return movieDao.register(title, premierDate);
    }
}
