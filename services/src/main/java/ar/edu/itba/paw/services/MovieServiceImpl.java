package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.MovieCategory;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private MovieCategoryDao movieCategoryDao;

    @Transactional
    @Override
    public Movie register(String title, String originalTitle, long tmdbId, String imdbId, String originalLanguage,
                          String overview, float popularity, float runtime, float voteAverage, LocalDate releaseDate, Collection<Long> categories) {

        return movieDao.register(title, originalTitle,  tmdbId,  imdbId,  originalLanguage,
                 overview,  popularity,  runtime,  voteAverage,  releaseDate,  categories);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Movie> findMovieById(long id) {
        return movieDao.findMovieById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Movie> getAllMovies(int pageNumber, int pageSize) {
        return movieDao.getAllMovies(MovieDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Movie> findMoviesByPost(Post post) {
        return movieDao.findMoviesByPost(post);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Movie> getAllMoviesNotPaginated() {
        return movieDao.getAllMoviesNotPaginated();
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<MovieCategory> getAvailableCategories() {
        return movieCategoryDao.getAllCategories();
    }
}
