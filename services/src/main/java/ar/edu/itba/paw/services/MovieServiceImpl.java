package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.MovieCategory;
import ar.edu.itba.paw.models.PaginatedCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieServiceImpl.class);

    private static final String DEFAULT_POSTER_PATH = "";
    private static final String POSTER_SECURITY_TAG = "POSTER";

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private MovieCategoryDao movieCategoryDao;

    @Autowired
    private ImageService imageService;

    @Transactional
    @Override
    public Movie register(String title, String originalTitle, long tmdbId, String imdbId, String originalLanguage,
                          String overview, float popularity, float runtime, float voteAverage, LocalDate releaseDate, Collection<Long> categoriesId) {

        final Collection<MovieCategory> categories = movieCategoryDao.findCategoriesById(categoriesId);

        final Movie movie = movieDao.register(title, originalTitle,  tmdbId,  imdbId,  originalLanguage,
                 overview,  popularity,  runtime,  voteAverage,  releaseDate,  new HashSet<>(categories));

        LOGGER.info("Created Movie {}", movie.getId());

        return movie;
    }

    @Transactional
    @Override
    public Optional<byte[]> getPoster(long posterId) {

        LOGGER.info("Accessing Movie Poster {}. (Default {})", posterId, posterId == Movie.DEFAULT_POSTER_ID);

        if(posterId == Movie.DEFAULT_POSTER_ID)
            return Optional.of(imageService.getImage(DEFAULT_POSTER_PATH));

        else
            return imageService.getImage(posterId, POSTER_SECURITY_TAG);
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
    public Collection<Movie> getAllMoviesNotPaginated() {
        return movieDao.getAllMoviesNotPaginated();
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<MovieCategory> getAvailableCategories() {
        return movieCategoryDao.getAllCategories();
    }
}
