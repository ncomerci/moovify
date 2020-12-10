package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.interfaces.services.exceptions.InvalidSortCriteriaException;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.MovieCategory;
import ar.edu.itba.paw.models.PaginatedCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class MovieServiceImpl implements MovieService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieServiceImpl.class);

    private static final String DEFAULT_POSTER_PATH = "/images/defaultPoster.jpg";

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private MovieCategoryDao movieCategoryDao;

    @Autowired
    private ImageService imageService;

    private final static Map<String, MovieDao.SortCriteria> sortCriteriaMap = initializeSortCriteriaMap();

    private static Map<String, MovieDao.SortCriteria> initializeSortCriteriaMap() {
        final Map<String, MovieDao.SortCriteria> sortCriteriaMap = new LinkedHashMap<>();

        sortCriteriaMap.put("title", MovieDao.SortCriteria.TITLE);
        sortCriteriaMap.put("newest", MovieDao.SortCriteria.NEWEST);
        sortCriteriaMap.put("oldest", MovieDao.SortCriteria.OLDEST);
        sortCriteriaMap.put("mostPosts", MovieDao.SortCriteria.POST_COUNT);

        return sortCriteriaMap;
    }

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
    public void updatePoster(Movie movie, byte[] newPoster, String type) {

        Image poster = null;

        if(newPoster.length > 0)
            poster = imageService.uploadImage(newPoster, type);

        movie.setPoster(poster);

        LOGGER.info("Movie's {} Poster was Updated to {}", movie.getId(), poster == null ? 0 : poster.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<byte[]> getPoster(Movie movie) {

        final long movieId = movie.getPosterId();

        LOGGER.info("Accessing Movie Poster {}. (Default {})", movieId, movieId == Movie.DEFAULT_POSTER_ID);

        final Optional<byte[]> poster;

        if(movieId == Movie.DEFAULT_POSTER_ID) {
            poster = imageService.findImageByPath(DEFAULT_POSTER_PATH);

            if(!poster.isPresent())
                throw new RuntimeException("Failed loading default movie poster");
        }

        else
            poster = imageService.findImageById(movieId);

        return poster;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Movie> findMovieById(long id) {
        return movieDao.findMovieById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Movie> getAllMovies(String sortCriteria, int pageNumber, int pageSize) {
        return movieDao.getAllMovies(getMovieSortCriteria(sortCriteria), pageNumber, pageSize);
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

    @Override
    public MovieDao.SortCriteria getMovieSortCriteria(String sortCriteriaName) {
        if (sortCriteriaName != null && sortCriteriaMap.containsKey(sortCriteriaName))
            return sortCriteriaMap.get(sortCriteriaName);

        else
            throw new InvalidSortCriteriaException();
    }

    @Override
    public Collection<String> getMovieSortOptions() {
        return sortCriteriaMap.keySet();
    }

}
