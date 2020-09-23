package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.MovieCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private MovieCategoryDao movieCategoryDao;


    @Override
    public Optional<Movie> findById(long id) {
        return movieDao.findById(id);
    }

    @Override
    public Movie register(String title, String originalTitle, long tmdbId, String imdbId, String originalLanguage,
                          String overview, float popularity, float runtime, float voteAverage, LocalDate releaseDate, Collection<Long> categories) {
        return movieDao.register(title, originalTitle,  tmdbId,  imdbId,  originalLanguage,
                 overview,  popularity,  runtime,  voteAverage,  releaseDate,  categories);
    }

    @Override
    public Collection<Movie> getAllMovies() {
        return movieDao.getAllMovies();
    }

    @Override
    public Collection<Movie> findMoviesByPostId(long postId) {
        return movieDao.findMoviesByPostId(postId);
    }

    @Override
    public Collection<MovieCategory> getAvailableCategories() {
        return movieCategoryDao.getAllCategories();
    }
}
