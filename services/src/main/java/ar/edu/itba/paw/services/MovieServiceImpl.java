package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.models.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieDao movieDao;


    @Override
    public Optional<Movie> findById(long id) {
        return movieDao.findById(id);
    }

    @Override
    public Movie register(String title, LocalDate premierDate) { return movieDao.register(title, premierDate); }

    @Override
    public Collection<Movie> getAllMovies() {
        return movieDao.getAllMovies();
    }

    @Override
    public Collection<Movie> findMoviesByPostId(long postId) {
        return movieDao.findMoviesByPostId(postId);
    }
}
